package com.example.administrator.stubapp.download;

import android.util.Log;

import com.example.administrator.stubapp.bean.LiveLesson;
import com.example.administrator.stubapp.db.LiveLessonManager;
import com.example.administrator.stubapp.download.downloadListener.DownloadInterceptor;
import com.example.administrator.stubapp.download.exception.RetryWhenNetworkException;
import com.example.administrator.stubapp.download.subscribers.ProgressDownSubscriber;
import com.example.administrator.stubapp.utils.FileManager;
import com.example.administrator.stubapp.utils.StubPreferences;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 文件描述：http下载处理类
 * 作者：Created by BiJingCun on 2018/9/20.
 */

public class HttpDownManager{
    //记录下在数据
    private Set<LiveLesson> downInfos;
    //回调队列
    private HashMap<String, ProgressDownSubscriber> subMap;
    //数据库类；
    private LiveLessonManager dbUtil;
    //单利对象
    private volatile static HttpDownManager INSTANCE;

    private List<DownloadObserver> mObservers = new ArrayList<>();
    private LinkedHashMap mTsmap;

    private HttpDownManager() {
        downInfos = new HashSet<>();
        subMap = new HashMap<>();
        dbUtil = LiveLessonManager.getInstance();
    }

    /**
     * 获取单利
     */
    public static HttpDownManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpDownManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpDownManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 开始下载
     * //http://cdnaliyunv.tianguiedu.com/201803/e342b754-6ae3-4d55-bef6-dbfef14d94c0/low.m3u8
     * // id  153
     */
    public void startDown(final LiveLesson info) {
        //正在下载不做处理
        if (info == null || subMap.get(info.getUrl()) != null) {
            subMap.get(info.getUrl()).setDownInfo(info);
            return;
        }
        //添加回调处理类
        final ProgressDownSubscriber subscriber = new ProgressDownSubscriber(info);
        subscriber.setHttpDownManager(this);
        //记录回调sub 如果有多个任务下载,将任务放在map结合中
        subMap.put(info.getUrl(), subscriber);
        StubPreferences.setStringValue(String.valueOf(info.getPath()),"");//保存下载进度

        //1先下载 .m3u8 文件
        String path = info.getPath();
        path = path + "/";
        String loadPath3u8 = "http://cdnaliyunv.tianguiedu.com/201803/e342b754-6ae3-4d55-bef6-dbfef14d94c0/low.m3u8";
        final String finalPath = path;
        OkGo.<File>get(loadPath3u8).tag(HttpDownManager.this).execute(new FileCallback(finalPath, "output.m3u8") {
            @Override
            public void onSuccess(Response<File> response) {
                Log.d("XIAZAIZHONG", response.body().toString());
            }

            @Override
            public void onError(Response<File> response) {
                super.onError(response);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                String s = finalPath + "output.m3u8";
                final String fileContent = FileManager.ReadTxtFile(s);
                Pattern kp = Pattern
                        .compile("(?<=\").*?.key(?=\")");
                final Matcher km = kp.matcher(fileContent);
                if (km.find()) {
                    //下载KEY文件保存到本地
                    final String keyPath = finalPath + "mykey.key";
                    String downKeyUrl = "http://cdnaliyunv.tianguiedu.com" + km.group();
                    OkGo.<File>get(downKeyUrl).tag(HttpDownManager.this).execute(new FileCallback(finalPath, "mykey.key") {
                        @Override
                        public void onSuccess(Response<File> response) {

                        }

                        @Override
                        public void onError(Response<File> response) {
                            super.onError(response);
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            //替换KEY下载路径为保存路径
                            String group = km.group();
                            String replacecontent = fileContent.replace(group, keyPath);
                            //替换视频片段的下载地址为保存路径
                            Pattern p = Pattern
                                    .compile("[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}/output\\d+.ts");
                            Matcher m = p.matcher(replacecontent);
                            int i = 0;
                            ConcurrentHashMap<String, String> downloaderHashMap = info.getDownloaderHashMap();
                            while (m.find()) {

                                String videoPath = i + ".ts";
                                downloaderHashMap.put(videoPath, m.group());
                                i++;
                                replacecontent = replacecontent.replace(m.group(), videoPath);//替换后的文档内容
                            }
                            info.setDownloaderHashMap(downloaderHashMap);
                            //保存替换完的文件
                            String newSavePath = info.getPath() + "/newoutput.m3u8";
                            FileManager.WriteTxtFile(replacecontent, newSavePath);
                            info.setDownIndex(0 + "");
                            //获取service,多次请求公用一个service
                            final HttpDownService httpDownService;
                            if (downInfos.contains(info)) {
                                httpDownService = info.getService();
                            } else {
                                //得到RX对象上一次下载的位置开始下载
                                DownloadInterceptor interceptor = new DownloadInterceptor(subscriber,downloaderHashMap.keySet().size(),String.valueOf(info.getId()));
                                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                                //手动创建一个OkHttpClient并设置超时时间
                                builder.connectTimeout(6, TimeUnit.SECONDS);
                                builder.addInterceptor(interceptor);

                                Retrofit retrofit = new Retrofit.Builder()
                                        .client(builder.build())
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                    .baseUrl(Urls.SERVER_URL)
                                        .baseUrl("http://cdnaliyunv.tianguiedu.com/")
                                        .build();
                                httpDownService = retrofit.create(HttpDownService.class);
                                info.setService(httpDownService);
                                downInfos.add(info);
                            }
                            downLoadTsVideos(info, downloaderHashMap, subscriber, httpDownService);
                        }
                    });
                }

            }
        });

    }

    private void downLoadTsVideos(final LiveLesson mInfo, final ConcurrentHashMap<String, String> mDownloaderHashMap, ProgressDownSubscriber mSubscriber, final HttpDownService mHttpDownService) {
        //得到RX对象上一次下载的位置开始下载
        final String downIndex = mInfo.getDownIndex();
        int size = mDownloaderHashMap.entrySet().size();//一共的视频下载个数
        int noDownCount = size - Integer.parseInt(downIndex);//没有下载的视频数
        List<String> values = new ArrayList<>();
        String indexText="";
        boolean toDown=false;
        for (String key : mDownloaderHashMap.keySet()) {
            if(key.contains(downIndex)&&noDownCount!=0){
                toDown=true;
            }
            if(noDownCount==0){
                toDown=false;
            }
            if(toDown){
                String value = mDownloaderHashMap.get(key);
                values.add(value);
                indexText=indexText+key+",";
            }
        }
        StubPreferences.setStringValue(String.valueOf(mInfo.getId()),indexText);
        Observable.from(values)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .concatMap(new Func1<String, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(String tsUrl) {
                        String url = new String(MessageFormat.format("{0}?app=android&ukey={1}{2}", "http://cdnaliyunv.tianguiedu.com/201803/e342b754-6ae3-4d55-bef6-dbfef14d94c0/" + tsUrl,
                                "461", System.currentTimeMillis()));
                        Observable<ResponseBody> download = mHttpDownService.download("bytes=0" + "-", url);
                        return download;
                    }
                })
                .retryWhen(new RetryWhenNetworkException())// 是否重试(true)// 是否重试
                .map(new Func1<ResponseBody, Object>() {
                    @Override
                    public Object call(ResponseBody mResponseBody) {
                        try {
                            //写文件(真正写是在这里)
                            String stringValue = StubPreferences.getStringValue(String.valueOf(mInfo.getId()));
                            String[] split = stringValue.split(",");
                            String substring = split[0].substring(0, 1);
                            int i = Integer.parseInt(substring);
                            writeCache(mResponseBody, new File(mInfo.getPath() + "/" + i + ".ts"), mInfo,i,stringValue);
                        } catch (IOException e) {
                            /*失败抛出异常*/
                            throw new RuntimeException(e.getMessage());
                        }
                        return mInfo;
                    }
                })
                //回调线程
                .observeOn(AndroidSchedulers.mainThread())
                //数据回调
                .subscribe(mSubscriber);

    }


    /**
     * 写入文件
     *
     * @param mResponseBody
     * @param mFile
     * @param mInfo
     */
    public static void writeCache(ResponseBody mResponseBody, File mFile, LiveLesson mInfo,int i,String value) throws IOException {
        //获取文件所在的目录 不存在创建
        if (!mFile.getParentFile().exists()) {
            mFile.getParentFile().mkdirs();
        }
        //片段视频文件总长度
        long allLenght = mResponseBody.contentLength();
        try {
            FileChannel channelOut = null;
            RandomAccessFile randomAccessFile = null;
            //mFiel 文件本身  rwd  以读取、写入方式打开指定文件
            randomAccessFile = new RandomAccessFile(mFile, "rwd");
            channelOut = randomAccessFile.getChannel();
            MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE,
                    0, allLenght);
            byte[] buffer = new byte[1024 * 8];
            int len;
            int record = 0;
            while ((len = mResponseBody.byteStream().read(buffer)) != -1) {
                mappedBuffer.put(buffer, 0, len);
                record += len;
            }
            mResponseBody.byteStream().close();
            if (channelOut != null) {
                channelOut.close();
            }
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
            String replace = value.replace(i + ".ts,", "");
            StubPreferences.setStringValue(String.valueOf(mInfo.getId()), replace);
        } catch (FileNotFoundException mE) {
            mE.printStackTrace();
        }
    }



    /**
     * 停止全部下载
     */
    public void stopAllDown() {
        for (LiveLesson downInfo : downInfos) {
            stopDown(downInfo);
        }
        subMap.clear();
        downInfos.clear();
    }

    /**
     * 停止下载
     *
     * @param mDownInfo
     */
    private void stopDown(LiveLesson mDownInfo) {
        if (mDownInfo == null) return;
        mDownInfo.setState(DownState.PAUSE);
        if (mDownInfo.getListener() != null)
            mDownInfo.getListener().onPuse();
        notifyDownloadStateChanged(mDownInfo);
        if (subMap.containsKey(mDownInfo.getUrl())) {
            ProgressDownSubscriber subscriber = subMap.get(mDownInfo.getUrl());
            subscriber.unsubscribe();
            subMap.remove(mDownInfo.getUrl());
        }
        //保存数据库信息和本地文件
        dbUtil.updateLiveLesson(mDownInfo);
    }


    /**
     * 暂停全部下载
     */
    public void pauseAll() {
        for (LiveLesson downInfo : downInfos) {
            pause(downInfo);
        }
        subMap.clear();
        downInfos.clear();
    }

    /**
     * 暂停下载
     *
     * @param mDownInfo
     */
    public void pause(LiveLesson mDownInfo) {
        if (mDownInfo == null) return;
        mDownInfo.setState(DownState.PAUSE);
        if (mDownInfo.getListener() != null)
            mDownInfo.getListener().onPuse();
        notifyDownloadStateChanged(mDownInfo);
        if (subMap.containsKey(mDownInfo.getUrl())) {
            ProgressDownSubscriber subscriber = subMap.get(mDownInfo.getUrl());
            subscriber.unsubscribe();
            subMap.remove(mDownInfo.getUrl());
        }
        //这里将mDownInfo写入数据库中
        dbUtil.updateLiveLesson(mDownInfo);
    }


    /**
     * 移除下载数据
     *
     * @param info
     */
    public void remove(LiveLesson info) {
        subMap.remove(info.getUrl());
        downInfos.remove(info);
    }


    /**
     * 返回全部正在下载的数据
     *
     * @return
     */
    public Set<LiveLesson> getDownInfos() {
        return downInfos;
    }

    /**
     * 注册观察者
     */
    public void registerObserver(DownloadObserver mObserver) {
        synchronized (mObservers) {
            if (!mObservers.contains(mObserver)) {
                mObservers.add(mObserver);
            }
        }
    }

    /**
     * 反注册观察者
     */
    public void unRegisterObserver(DownloadObserver mObserver) {
        synchronized (mObserver) {
            if (mObservers.contains(mObserver)) {
                mObservers.remove(mObserver);
            }
        }
    }

    /**
     * 当下载状态发生改变的时候回调
     */
    public void notifyDownloadStateChanged(LiveLesson info) {
        synchronized (mObservers) {
            for (DownloadObserver observer : mObservers) {
                observer.onDownloadStateChanged(info);
            }
        }
    }

    /**
     * 当下载进度发生改变的时候回调
     */
    public void notifyDownloadProgressed(LiveLesson info) {
        synchronized (mObservers) {
            for (DownloadObserver observer : mObservers) {
                observer.onDownloadProgressed(info);
            }
        }
    }


    public interface DownloadObserver {
        void onDownloadStateChanged(LiveLesson info);

        void onDownloadProgressed(LiveLesson info);
    }


}

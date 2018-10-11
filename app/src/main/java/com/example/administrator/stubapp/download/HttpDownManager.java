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
    public void startDown(final LiveLesson info,boolean hasDown) {
        //正在下载不做处理
//        if (info == null || subMap.get(info.getUrl()) != null) {
//            ProgressDownSubscriber subscriber = subMap.get(info.getUrl());
//            subscriber.setDownInfo(info);
//            return;
//        }
        //添加回调处理类
        final ProgressDownSubscriber subscriber = new ProgressDownSubscriber(info);
        subscriber.setHttpDownManager(this);
        //记录回调sub 如果有多个任务下载,将任务放在map结合中
        subMap.put(info.getUrl(), subscriber);
        //用来记录没有下载的.ts视频片段默认为空
        StubPreferences.setStringValue(String.valueOf(info.getPath()),"");
        if(hasDown){
            LinkedHashMap downloaderHashMap=info.getDownloaderHashMap();
            HttpDownService httpDownService = info.getService();
            int i = Integer.parseInt(info.getDownIndex());
            info.setDownloaderHashMap(downloaderHashMap);
            info.setDownIndex(String.valueOf(i));
            downLoadTsVideos(info, downloaderHashMap, subscriber);
            return;
        }
        // 没有下载过的 先下载 .m3u8 文件
        String loadPath3u8 = "http://cdnaliyunv.tianguiedu.com/201802/3ed31f9f-d30b-4c06-8eba-cd2e39f2a146/low.m3u8";
        //用来存储 下载完的视频文件；
        String path = info.getPath()+"/";
        OkGo.<File>get(loadPath3u8).tag(HttpDownManager.this).execute(new FileCallback(path, "output.m3u8") {
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
                //下载完的 .m3u8文件路径
                String saveM3u8Path=info.getPath()+"/"+"output.m3u8";
                //读取下载的文件内容
                final String m3u8Content = FileManager.ReadTxtFile(saveM3u8Path);
                //正则 匹配.m3u8文件中的key
                Pattern kp = Pattern
                        .compile("(?<=\").*?.key(?=\")");
                final Matcher km = kp.matcher(m3u8Content);
                if (km.find()) {
                    //下载KEY文件保存到本地
                    String rootFile = info.getPath() + "/";
                    final String keyPath =  rootFile+ "mykey.key";
                    //下载KEY 的路径
                    String downKeyUrl = "http://cdnaliyunv.tianguiedu.com" + km.group();
                    OkGo.<File>get(downKeyUrl).tag(HttpDownManager.this).execute(new FileCallback(rootFile, "mykey.key") {
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
                            //1替换KEY下载路径为保存路径
                            String group = km.group();
                            String replacecontent = m3u8Content.replace(group, keyPath);
                            //2替换视频片段的下载地址为保存路径
                            Pattern p = Pattern
                                    .compile("[a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12}/output\\d+.ts");
                            Matcher m = p.matcher(replacecontent);
                            int i = 0;
                            LinkedHashMap<String, String> downloaderHashMap = info.getDownloaderHashMap();
                            while (m.find()) {
                                String videoPath = i + ".ts";
                                downloaderHashMap.put(videoPath, m.group());
                                i++;
                                replacecontent = replacecontent.replace(m.group(), videoPath);//替换后的文档内容
                            }
                            info.setDownloaderHashMap(downloaderHashMap);
                            //3保存替换完的文件
                            String newSavePath = info.getPath() + "/newoutput.m3u8";
                            FileManager.WriteTxtFile(replacecontent, newSavePath);
                            info.setDownIndex(0 + "");
                            //4下载片段

                            //下载的拦截器
                            DownloadInterceptor interceptor = new DownloadInterceptor(subscriber,downloaderHashMap.keySet().size(),String.valueOf(info.getId()));
                            OkHttpClient.Builder builder = new OkHttpClient.Builder();
                            builder.connectTimeout(6, TimeUnit.SECONDS);
                            builder.addInterceptor(interceptor);
                            Retrofit retrofit = new Retrofit.Builder()
                                    .client(builder.build())
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                    .baseUrl(Urls.SERVER_URL)
                                    .baseUrl("http://cdnaliyunv.tianguiedu.com/")
                                    .build();
                            HttpDownService httpDownService = retrofit.create(HttpDownService.class);
                                info.setService(httpDownService);
                                downInfos.add(info);
                                //开始下载
                            downLoadTsVideos(info, downloaderHashMap, subscriber);
                        }
                    });
                }

            }
        });

    }

    private void downLoadTsVideos(final LiveLesson mInfo, final LinkedHashMap<String, String> mDownloaderHashMap, ProgressDownSubscriber mSubscriber) {
        //得到RX对象上一次下载的位置开始下载
        final String downIndex = mInfo.getDownIndex();
        int size = mDownloaderHashMap.entrySet().size();//一共的视频下载个数
        mInfo.setSize((long) size);
        int noDownCount = size - Integer.parseInt(downIndex);//没有下载的视频数
        dbUtil.updateLiveLesson(mInfo);
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
        //保存还没有下载的视频序号
        StubPreferences.setStringValue(String.valueOf(mInfo.getId()),indexText);
        //通过Rxjava 有序的concatMap 方法来一个一个片段下载视频片段
        Observable.from(values)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .concatMap(new Func1<String, Observable<ResponseBody>>() {
                    @Override
                    public Observable<ResponseBody> call(String tsUrl) {
                        //视频的片段路径的拼接；
                        String url = new String(MessageFormat.format("{0}?app=android&ukey={1}{2}", "http://cdnaliyunv.tianguiedu.com/201802/3ed31f9f-d30b-4c06-8eba-cd2e39f2a146/" + tsUrl,
                                "461", System.currentTimeMillis()));
                        //获取到service
                        HttpDownService service = mInfo.getService();
                        //每一个片段都是重弟0个字节开始读取下载
                        Observable<ResponseBody> download = service.download("bytes=0" + "-", url);
                        return download;
                    }
                })
                .retryWhen(new RetryWhenNetworkException())// 是否重试(true)// 是否重试
                .map(new Func1<ResponseBody, Object>() {
                    @Override
                    public Object call(ResponseBody mResponseBody) {
                        try {
                            //下载完的ResposneBody 写到文件里

                            //先取出保存的应该下载的文件序列号
                            String stringValue = StubPreferences.getStringValue(String.valueOf(mInfo.getId()));
                            //得到数组
                            String[] split = stringValue.split(",");
                            //得到应该下载的位置
                            String[] split1 = split[0].split("\\.");
                            String substring = split1[0];
                            int i = Integer.parseInt(substring);
                            //写入文件；
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
            //片段下载完后保存位置
            String s = i + ".ts,";
            String substring = value.substring(s.length(),value.length());
            StubPreferences.setStringValue(String.valueOf(mInfo.getId()), substring);
        } catch (Exception mE) {
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
        String stringValue = StubPreferences.getStringValue(String.valueOf(mDownInfo.getId()));
        LiveLesson liveLesson = dbUtil.queryVideo(mDownInfo.getId());
        Long size = liveLesson.getSize();
        String[] split = stringValue.split(",");
        String s = String.valueOf(size - split.length);
        mDownInfo.setDownIndex(s);
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

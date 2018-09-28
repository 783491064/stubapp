package com.example.administrator.stubapp.download;

import com.example.administrator.stubapp.bean.LiveLesson;
import com.example.administrator.stubapp.db.LiveLessonManager;
import com.example.administrator.stubapp.download.downloadListener.DownloadInterceptor;
import com.example.administrator.stubapp.download.exception.RetryWhenNetworkException;
import com.example.administrator.stubapp.download.subscribers.ProgressDownSubscriber;
import com.example.administrator.stubapp.http.HttpManager;
import com.example.administrator.stubapp.http.Urls;
import com.example.administrator.stubapp.utils.FileManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Url;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 文件描述：http下载处理类
 * 作者：Created by BiJingCun on 2018/9/20.
 */

public class HttpDownManager {
    //记录下在数据
    private Set<LiveLesson> downInfos;
    //回调队列
    private HashMap<String, ProgressDownSubscriber> subMap;
    //数据库类；
    private LiveLessonManager dbUtil;
    //单利对象
    private volatile static HttpDownManager INSTANCE;

    private List<DownloadObserver> mObservers = new ArrayList<>();

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
     */
    public void startDown(final LiveLesson info) {
        //1先下载 .m3u8 文件
        //2.替换掉ts
        //3.下载解密文件放到本地 并将路径添加到 本地.m3u8 文件

        //正在下载不做处理
//        http://cdnaliyunv.tianguiedu.com/201803/e342b754-6ae3-4d55-bef6-dbfef14d94c0/low.m3u8
        if (info == null || subMap.get(info.getUrl()) != null) {
            subMap.get(info.getUrl()).setDownInfo(info);
            return;
        }
        //添加回调处理类
        ProgressDownSubscriber subscriber = new ProgressDownSubscriber(info);
        subscriber.setHttpDownManager(this);
        //记录回调sub 如果有多个任务下载,将任务放在map结合中
        subMap.put(info.getUrl(), subscriber);
        //获取service,多次请求公用一个service
        HttpDownService httpDownService;
        if (downInfos.contains(info)) {
            httpDownService = info.getService();
        } else {
            DownloadInterceptor interceptor = new DownloadInterceptor(subscriber);
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            //手动创建一个OkHttpClient并设置超时时间
            builder.connectTimeout(6, TimeUnit.SECONDS);
            builder.addInterceptor(interceptor);

            Retrofit retrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                    .baseUrl(Urls.SERVER_URL)
                    .baseUrl("http://appdlc.hicloud.com/")
                    .build();
            httpDownService = retrofit.create(HttpDownService.class);
            info.setService(httpDownService);
            downInfos.add(info);
        }
        //得到RX对象上一次下载的位置开始下载

        httpDownService.download("bytes=" + info.getDownlength() + "-", info.getUrl())
                //指定线程
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                //失败后的重新连接
                .retryWhen(new RetryWhenNetworkException())// 是否重试(true)// 是否重试
                .map(new Func1<ResponseBody, Object>() {
                    @Override
                    public Object call(ResponseBody mResponseBody) {
                        try {
                            //写文件(真正写是在这里)
                            FileManager.writeCache(mResponseBody, new File(info.getPath()), info);
                        } catch (IOException e) {
                            /*失败抛出异常*/
                            throw new RuntimeException(e.getMessage());
                        }
                        return info;

                    }
                })
                //回调线程
                .observeOn(AndroidSchedulers.mainThread())
                //数据回调
                .subscribe(subscriber);


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

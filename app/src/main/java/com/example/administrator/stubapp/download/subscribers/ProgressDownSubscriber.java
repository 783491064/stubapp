package com.example.administrator.stubapp.download.subscribers;

import com.example.administrator.stubapp.bean.LiveLesson;
import com.example.administrator.stubapp.db.LiveLessonManager;
import com.example.administrator.stubapp.download.DownInfo;
import com.example.administrator.stubapp.download.DownState;
import com.example.administrator.stubapp.download.HttpDownManager;
import com.example.administrator.stubapp.download.downloadListener.DownloadProgressListener;
import com.example.administrator.stubapp.download.downloadListener.HttpDownOnNextListener;
import com.example.administrator.stubapp.http.HttpManager;

import java.lang.ref.SoftReference;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 文件描述：
 * 断点下载处理类Subscriber
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * 作者：Created by BiJingCun on 2018/9/20.
 */

public class ProgressDownSubscriber<T> extends Subscriber<T> implements DownloadProgressListener {
    //弱引用结果回调
    private SoftReference<HttpDownOnNextListener> mSubscriberOnNextListener;
    //下载的数据
    private LiveLesson downInfo;
    private HttpDownManager manager;

    public ProgressDownSubscriber(LiveLesson downInfo) {
        this.mSubscriberOnNextListener = new SoftReference<>(downInfo.getListener());
        this.downInfo = downInfo;
    }

    public void setHttpDownManager(HttpDownManager manager) {
        this.manager = manager;
    }

    public void setDownInfo(LiveLesson downInfo) {
        this.mSubscriberOnNextListener = new SoftReference<>(downInfo.getListener());
        this.downInfo = downInfo;
    }

    /**
     * 开始下载
     */
    @Override
    public void onStart() {
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onStart();
        }
        downInfo.setState(DownState.START);
    }

    /**
     * 下载完成
     */
    @Override
    public void onCompleted() {
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onComplete();
        }
        HttpDownManager.getInstance().remove(downInfo);
        downInfo.setState(DownState.FINISH);
        LiveLessonManager.getInstance().updateLiveLesson(downInfo);
    }

    @Override
    public void update(long read, final long count, final boolean done) {
        if(done){
            downInfo.setDownlength(count);
        }else{
            downInfo.setDownlength(read);
        }
        //接受进度消息，造成UI阻塞，如果不需要显示进度可以去掉实现逻辑，减少压力
        rx.Observable.just(read).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long mLong) {
                        //如果暂停或者停止状态延迟，不需要继续发送回调，影响现实
                        if (downInfo.getState() == DownState.PAUSE || downInfo.getState() == DownState.STOP)
                            return;
                        if(done){
                            downInfo.setState(DownState.DOWN);
                        }
                        if (manager != null) {
                            manager.notifyDownloadStateChanged(downInfo);
                            manager.notifyDownloadProgressed(downInfo);
                        }
                        if (mSubscriberOnNextListener.get() != null) {
                            mSubscriberOnNextListener.get().updateProgress(mLong, count);
                        }
                    }
                });
    }

    /**
     * 对错误的统一处理
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onError(e);
        }
        HttpDownManager.getInstance().remove(downInfo);
        downInfo.setState(DownState.ERROR);
        LiveLessonManager.getInstance().updateLiveLesson(downInfo);
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     */
    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onNext(t);
        }
    }
}

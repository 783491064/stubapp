package com.example.administrator.stubapp.download.downloadListener;

/**
 * 文件描述：下载过程中的回调处理
 * 作者：Created by BiJingCun on 2018/9/20.
 */

public abstract class HttpDownOnNextListener<T> {
    /**
     * 成功后回调方法
     */
    public abstract void onNext(T t);

    /**
     * 开始下载
     */
    public abstract void onStart();

    /**
     * 完成下载
     */
    public abstract void onComplete();

    /**
     * 下载进度
     */
    public abstract void updateProgress(long readLength,long counLength);

    /**
     * 下载失败或错误
     * 主动调用更加灵活
     */
    public void onError(Throwable e){}

    /**
     * 暂停下载
     */
    public void onPuse(){}

    /**
     * 停止下载销毁
     */
    public void onStop(){}

}

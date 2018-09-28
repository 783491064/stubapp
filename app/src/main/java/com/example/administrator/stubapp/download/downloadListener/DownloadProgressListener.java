package com.example.administrator.stubapp.download.downloadListener;

/**
 * 文件描述：进度回调处理
 * 作者：Created by BiJingCun on 2018/9/20.
 */

public interface DownloadProgressListener {
    //下载进度
    void update(long read,long count,boolean done);
}

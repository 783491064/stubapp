package com.example.administrator.stubapp.http.callbacks;

import com.example.administrator.stubapp.utils.DebugUtil;
import com.lzy.okgo.model.Progress;
import com.lzy.okserver.download.DownloadListener;

import java.io.File;

/**
 * 文件描述：OkGo下载监听器
 * 作者：Created by BiJingCun on 2018/9/19.
 */

public class LogDownloadListener extends DownloadListener {
    private static final String TAG="LogDownloadListener";
    public LogDownloadListener() {
        super(TAG);
    }

    @Override
    public void onStart(Progress progress) {
        DebugUtil.d(TAG,"onStart: 开始下载");
    }

    @Override
    public void onProgress(Progress progress) {
        DebugUtil.d(TAG,"onProgress:");
    }

    @Override
    public void onError(Progress progress) {
        DebugUtil.d(TAG,"onError:");
        progress.exception.printStackTrace();
    }

    @Override
    public void onFinish(File mFile, Progress progress) {
        DebugUtil.d(TAG,"onFinish:");
    }

    @Override
    public void onRemove(Progress progress) {
        DebugUtil.d(TAG,"onRemove:");
    }
}

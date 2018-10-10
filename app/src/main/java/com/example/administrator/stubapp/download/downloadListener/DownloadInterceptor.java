package com.example.administrator.stubapp.download.downloadListener;

import com.example.administrator.stubapp.download.subscribers.ProgressDownSubscriber;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 文件描述：成功回调处理
 * 作者：Created by BiJingCun on 2018/9/21.
 */

public class DownloadInterceptor implements Interceptor{
    private DownloadProgressListener listener;
    private int size=0;
    private String id;

    public DownloadInterceptor(DownloadProgressListener listener,int size,String id) {
        this.listener = listener;
        this.size=size;
        this.id=id;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse=chain.proceed(chain.request());

        return originalResponse.newBuilder()
                .body(new DownloadResponseBody(originalResponse.body(), listener,size,id))
                .build();
    }
}

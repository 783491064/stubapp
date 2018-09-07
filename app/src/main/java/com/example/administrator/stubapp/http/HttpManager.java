package com.example.administrator.stubapp.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 文件描述:网络请求管理类
 * 作者：Created by BiJingCun on 2018/9/7.
 */

public class HttpManager {

    private TGservice mTGservice;

    private HttpManager() {
    }

    private volatile static HttpManager httpHelper;

    public static HttpManager getInstance() {
        if (httpHelper == null) {
            synchronized (HttpManager.class) {
                if (httpHelper == null)
                    httpHelper = new HttpManager();
            }
        }
        return httpHelper;
    }

    public TGservice initRetrofit() {
        // 设置 Log 拦截器，可以用于以后处理一些异常情况
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                // 设置拦截器
                .retryOnConnectionFailure(true)// 是否重试
                .connectTimeout(10, TimeUnit.SECONDS)        // 连接超时事件
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();
        String baseUrl = Urls.SERVER_URL;

        mTGservice = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build().create(TGservice.class);
        return mTGservice;
    }


}

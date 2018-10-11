package com.example.administrator.stubapp.application;

import android.app.Application;
import android.content.Context;

import com.example.administrator.stubapp.utils.CrashHandler;
import com.example.administrator.stubapp.utils.StubPreferences;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;

import io.vov.vitamio.Vitamio;
import okhttp3.OkHttpClient;

/**
 * 文件描述：自定义项目的Application 初始化一些基本信息
 * 作者：Created by BiJingCun on 2018/8/16.
 */

public class StubApplication extends Application {


    public static Context mContext;
    // TODO: 2017/4/26  正式发布时关闭  统一关闭调试
    public static boolean IsDebug = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        //初始化SharedPreferences
        StubPreferences.initPreferences(mContext);
        //OKGO的基本配置
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkGo.getInstance().init(this)                       //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3);
//        CrashHandler.getInstance().init(this);
    }
}

package com.example.administrator.stubapp.application;

import android.app.Application;
import android.content.Context;

import com.example.administrator.stubapp.utils.StubPreferences;

/**
 * 文件描述：
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
//        CrashHandler.getInstance().init(this);
    }
}

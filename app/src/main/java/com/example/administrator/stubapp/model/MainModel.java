package com.example.administrator.stubapp.model;


import com.example.administrator.stubapp.bean.AppBannerResult;
import com.example.administrator.stubapp.http.HttpManager;
import com.example.administrator.stubapp.http.RxSchedulers;
import com.google.gson.Gson;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 文件描述：主页面 M
 * 作者：Created by BiJingCun on 2018/9/7.
 */

public class MainModel {
    public Observable<AppBannerResult> getData(String s, String m) {
        return HttpManager
                .getInstance()
                .initRetrofit()
                .getUserLearn(s, m)
                .compose(RxSchedulers.<String>switchThread())
                .map(new Func1<String, AppBannerResult>() {
                    @Override
                    public AppBannerResult call(String mS) {
                        AppBannerResult appBannerResult = new Gson().fromJson(mS, AppBannerResult.class);
                        return appBannerResult;
                    }
                });
    }
}

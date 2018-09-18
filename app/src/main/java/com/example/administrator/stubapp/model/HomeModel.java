package com.example.administrator.stubapp.model;

import com.example.administrator.stubapp.bean.AppBannerResult;
import com.example.administrator.stubapp.http.HttpManager;
import com.example.administrator.stubapp.http.RxSchedulers;
import com.google.gson.Gson;

import rx.Observable;
import rx.functions.Func1;

/**
 * 文件描述：homeFragment M
 * 作者：Created by BiJingCun on 2018/9/17.
 */

public class HomeModel {
    public Observable<AppBannerResult> getHomeData(String s, String m) {
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

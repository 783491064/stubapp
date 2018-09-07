package com.example.administrator.stubapp.model;


import com.example.administrator.stubapp.http.HttpManager;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 文件描述：主页面 M
 * 作者：Created by BiJingCun on 2018/9/7.
 */

public class MainModel{
    public Observable<String> getData(){
        return HttpManager.getInstance().initRetrofit().getUserLearn();
    }
}

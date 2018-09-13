package com.example.administrator.stubapp.http;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 文件描述：封装 Rx线程 使Rxjava的线程切换更简洁直接使用RxSchedulers
 * 作者：Created by BiJingCun on 2018/9/12.
 */

public class RxSchedulers {
    public static <T> Observable.Transformer<T, T> switchThread() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}

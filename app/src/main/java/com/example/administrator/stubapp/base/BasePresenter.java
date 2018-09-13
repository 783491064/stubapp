package com.example.administrator.stubapp.base;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * 文件描述：presenter 的基类
 * 作者：Created by BiJingCun on 2018/9/7.
 */

public class BasePresenter<V> {
    // p 层持有的View 对象
    public V view;
    //所有网络请求的 Subscription的管理类；
    private CompositeSubscription mCompositeSubscription;

    public void attch(V view) {
        this.view = view;
    }

    /**
     * 网络请求的Subscription 添加到同一管理类 mCompositeSubscription
     *
     * @param mSubscription
     */
    protected void addSubscribe(Subscription mSubscription) {
        if (null == mCompositeSubscription) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(mSubscription);
    }

    /**
     * 取消全部网络请求
     */
    protected void unSubscribe() {
        if (view != null) {
            view = null;
        }
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.clear();
        }
    }
}

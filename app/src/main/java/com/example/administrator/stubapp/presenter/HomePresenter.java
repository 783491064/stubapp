package com.example.administrator.stubapp.presenter;

import com.example.administrator.stubapp.base.BasePresenter;
import com.example.administrator.stubapp.bean.AppBannerResult;
import com.example.administrator.stubapp.model.HomeModel;
import com.example.administrator.stubapp.utils.DebugUtil;
import com.example.administrator.stubapp.view.HomeView;

import rx.Subscriber;
import rx.Subscription;

/**
 * 文件描述：首页的P
 * 作者：Created by BiJingCun on 2018/9/17.
 */

public class HomePresenter extends BasePresenter<HomeView> {
    private HomeModel model;
    public static final String TAG = "MainPresenter";

    @Override
    public void attch(HomeView view) {
        super.attch(view);
        model = new HomeModel();
    }

    public void getHomeData() {
        Subscription subscribe = model.getHomeData("","").subscribe(new Subscriber<AppBannerResult>() {
            @Override
            public void onStart() {
                view.showProgress();
            }

            @Override
            public void onCompleted() {
                view.cancleProgress();
                DebugUtil.d("获取数据", "数据信息完成");
            }

            @Override
            public void onError(Throwable e) {
                view.cancleProgress();
                DebugUtil.d("获取数据", "数据信息失败" + e);
            }

            @Override
            public void onNext(AppBannerResult mAppBannerResult) {
                DebugUtil.d("获取数据", "数据信息==============" + mAppBannerResult.toString());
            }
        });
        addSubscribe(subscribe);
    }
}

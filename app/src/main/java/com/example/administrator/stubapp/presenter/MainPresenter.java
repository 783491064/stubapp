package com.example.administrator.stubapp.presenter;


import com.example.administrator.stubapp.bean.AppBannerResult;
import com.example.administrator.stubapp.model.MainModel;
import com.example.administrator.stubapp.base.BasePresenter;
import com.example.administrator.stubapp.utils.DebugUtil;
import com.example.administrator.stubapp.view.MainView;
import rx.Subscriber;
import rx.Subscription;

/**
 * 文件描述： 主页面的P
 * 作者：Created by BiJingCun on 2018/9/7.
 */

public class MainPresenter extends BasePresenter<MainView> {
    private MainModel model;
    public static final String TAG = "MainPresenter";

    @Override
    public void attch(MainView view) {
        super.attch(view);
        model = new MainModel();
    }

    public void getData() {
        Subscription subscribe = model.getData("","").subscribe(new Subscriber<AppBannerResult>() {
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

package com.example.administrator.stubapp.presenter;

import com.example.administrator.stubapp.model.MainModel;
import com.example.administrator.stubapp.ui.base.BasePresenter;
import com.example.administrator.stubapp.utils.DebugUtil;
import com.example.administrator.stubapp.view.MainView;

import rx.Subscriber;
import rx.Subscription;

/**
 * 文件描述： 主页面的P
 * 作者：Created by BiJingCun on 2018/9/7.
 */

public class MainPresenter extends BasePresenter<MainView>{
    private MainModel model;
    public static final String TAG="MainPresenter";
    @Override
    public void attch(MainView view) {
        super.attch(view);
        model=new MainModel();
    }
    public void getData(){
        view.showDialog();
        Subscription subscribe = model.getData().subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                view.dismessDialog();
            }

            @Override
            public void onError(Throwable e) {
                DebugUtil.e(TAG, e.getLocalizedMessage());
            }

            @Override
            public void onNext(String mS) {
                view.showData();
            }
        });
        addSubscribe(subscribe);
    }
}

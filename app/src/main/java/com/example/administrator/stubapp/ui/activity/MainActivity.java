package com.example.administrator.stubapp.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.administrator.stubapp.R;
import com.example.administrator.stubapp.presenter.MainPresenter;
import com.example.administrator.stubapp.ui.base.BaseActivity;
import com.example.administrator.stubapp.ui.base.BaseMVPActivity;
import com.example.administrator.stubapp.ui.base.BasePresenter;
import com.example.administrator.stubapp.view.MainView;

public class MainActivity extends BaseMVPActivity<MainView,MainPresenter> implements MainView {

    private MainPresenter mPresenter;

    @Override
    protected void setStatusBarColor() {

    }

    @Override
    protected void preInit() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        mPresenter.getData();
    }

    @Override
    protected MainPresenter initPresenter() {
        mPresenter = new MainPresenter();
        mPresenter.attch(this);
        return mPresenter;
    }

    @Override
    public void showData() {

    }

    @Override
    public void showDialog() {

    }

    @Override
    public void dismessDialog() {

    }

    @Override
    public void onError(String e) {

    }
}

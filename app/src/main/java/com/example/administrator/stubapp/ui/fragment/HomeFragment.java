package com.example.administrator.stubapp.ui.fragment;

import android.app.DownloadManager;

import com.example.administrator.stubapp.R;
import com.example.administrator.stubapp.base.BaseMVPFragment;
import com.example.administrator.stubapp.base.BasePresenter;
import com.example.administrator.stubapp.http.callbacks.LogDownloadListener;
import com.example.administrator.stubapp.presenter.HomePresenter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;

import java.io.File;

/**
 * 文件描述：首页fragment
 * 作者：Created by BiJingCun on 2018/9/17.
 */

public class HomeFragment extends BaseMVPFragment {

    private HomePresenter mPresenter;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {

    }

    @Override
    public BasePresenter initPresenter() {
        mPresenter = new HomePresenter();
        return mPresenter;
    }

    @Override
    public void initListener() {

    }

    @Override
    protected void initData() {
        mPresenter.getHomeData();
        GetRequest<File> request = OkGo.<File>get("");
        OkDownload.request("id", request)
                .fileName("pdf" + ".pdf")
                .save()
                .register(new LogDownloadListener())
                .start();
    }

    @Override
    protected void onFragmentFirstVisible() {
        //第一次加载数据
        initData();
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible) {
            //可见的情况继续加载数据
        } else {
            //不可见了的情况停止加载数据

        }
    }
}

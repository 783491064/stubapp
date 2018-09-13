package com.example.administrator.stubapp.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 文件描述：fragment的基类
 * 作者：Created by BiJingCun on 2018/9/13.
 */

public abstract class BaseFragment extends Fragment {
    protected Activity mContext;
    protected View rootView;
    private Unbinder mBind;

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        mContext=context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView==null){
            rootView=inflater.inflate(getLayoutResource(),container,false);
        }
        mBind = ButterKnife.bind(this, rootView);
        initMVP();
        initView();
        initListener();
        initData();
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        detach();
        mContext = null;
    }

    /**
     * fargment 片段移除时
     */
    protected void detach() {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    /**
     * 获取布局
     * @return
     */
    protected abstract int getLayoutResource();

    /**
     * mvp初始化
     */
    protected void initMVP(){};

    /**
     * 初始化view
     */
    protected abstract void initView();

    /**
     * 初始化点击事件
     */
    public abstract void initListener();

    /**
     * 初始化数据
     */
    protected abstract void initData();
}

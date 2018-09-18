package com.example.administrator.stubapp.ui.fragment;

import com.example.administrator.stubapp.R;
import com.example.administrator.stubapp.base.BaseMVPFragment;
import com.example.administrator.stubapp.base.BasePresenter;
import com.example.administrator.stubapp.presenter.HomePresenter;

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
    }

    @Override
    protected void onFragmentFirstVisible() {
        //第一次加载数据
        initData();
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        if(isVisible){
            //可见的情况继续加载数据
        }else{
            //不可见了的情况停止加载数据

        }
    }
}

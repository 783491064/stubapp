package com.example.administrator.stubapp.base;

/**
 * 文件描述：fragment 的mvp初始化
 * 作者：Created by BiJingCun on 2018/9/13.
 */

public abstract class BaseMVPFragment<V,P extends BasePresenter<V>> extends BaseFragment {
    protected P p;

    @Override
    protected void initMVP() {
        p = initPresenter();
        if (p != null)
            p.attch((V) this);
    }

    /**
     * 简单页面无需mvp就不用管此方法即可
     */
    public abstract P initPresenter();

    /**
     * 释放资源
     */
    @Override
    protected void detach() {
        if(p!=null){
            p.unSubscribe();
        }
    }
}

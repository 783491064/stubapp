package com.example.administrator.stubapp.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.administrator.stubapp.view.IView;

/**
 * 文件描述：activity mvp 模式初始化
 * 作者：Created by BiJingCun on 2018/9/7.
 */

public abstract class BaseMVPActivity<V,P extends BasePresenter<V>> extends BaseActivity{
    protected P p;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initMVP() {
        p=initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        if (p != null)
            p.unSubscribe();
    }

    /**
     * 初始化 子类的presenter
     * 不需要P层的简单Activity可以直接返回null；
     * @return
     */
    protected abstract P initPresenter();
}

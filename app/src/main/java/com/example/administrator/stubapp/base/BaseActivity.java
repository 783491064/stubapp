package com.example.administrator.stubapp.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.administrator.stubapp.app.AppManager;
import com.example.administrator.stubapp.customView.LoadingDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 文件描述：activity的基类
 * 作者：Created by BiJingCun on 2018/9/6.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public Context mContext;
    private Unbinder mBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBeforeSetcontentView();
        setContentView(getLayoutId());
        mBind = ButterKnife.bind(this);
        mContext = this;
        initMVP();
        initView();
        initListener();
        initData();
    }

    /**
     * 加载布局之前的一些设置
     */
    protected void doBeforeSetcontentView() {
        //把activity放到application栈中管理
        AppManager.getInstance().addActivity(this);
        //无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //默认着色状态栏
        setStatusBarColor();
        //其他操作
        preInit();
    }

    //着色状态栏
    protected abstract void setStatusBarColor();

    //初始化View之前做一些事
    protected abstract void preInit();

    //获取布局文件
    protected abstract int getLayoutId();

    //BaseMVPActivity 初始化操作
    protected abstract void initMVP();

    //初始化view
    protected abstract void initView();

    //初始化点击事件
    protected abstract void initListener();

    //初始化数据
    protected abstract void initData();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this);
        mBind.unbind();
    }

    /**
     * startActivity
     */
    protected void readyGo(Class<?> clazz){
        Intent intent=new Intent(this,clazz);
        startActivity(intent);
    }

    /**
     * startActivity with bundle
     * @param clazz
     * @param mBundle
     */
    protected void readyGo(Class<?> clazz,Bundle mBundle){
        Intent intent=new Intent(this,clazz);
        if(null!=mBundle){
            intent.putExtras(mBundle);
        }
        startActivity(intent);
    }

    /**
     * startActivity then finish
     * @param clazz
     */
    protected void readyGoThenKill(Class<?> clazz){
        Intent intent=new Intent(this,clazz);
        startActivity(intent);
        finish();
    }

    /**
     * startActivityForResult
     * @param clazz
     * @param requestCode
     */
    protected void readyGoForResult(Class<?> clazz,int requestCode){
        Intent intent=new Intent(this,clazz);
        startActivityForResult(intent,requestCode);
    }


    /**
     * startActivityForResult with bundle
     * @param clazz
     * @param requestCode
     * @param mBundle
     */
    protected void readyGoForResult(Class<?> clazz ,int requestCode,Bundle mBundle){
        Intent intent = new Intent(this, clazz);
        if (null != mBundle) {
            intent.putExtras(mBundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 开启浮动加载进度条
     */
    public void startProgressDialog(String msg) {
        LoadingDialog.showLoadingProgress(this, msg, true);
    }

    /**
     * 停止浮动加载进度条
     */
    public void stopProgressDialog() {
        LoadingDialog.cancelLoading();
    }

}


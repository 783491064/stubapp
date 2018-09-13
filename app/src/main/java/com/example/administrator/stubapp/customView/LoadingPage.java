package com.example.administrator.stubapp.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.administrator.stubapp.R;
import com.example.administrator.stubapp.utils.DensityUtil;
import com.example.administrator.stubapp.utils.NetUtil;

/**
 * 文件描述：页面加载过程页面展示基类  无网络   没登录  被踢  数据为空
 * 作者：Created by BiJingCun on 2018/9/12.
 */

public abstract class LoadingPage extends FrameLayout implements View.OnClickListener{
    private Context context;
    private View noNetView;
    private View loginView;
    private View empty_view;

    public LoadingPage(Context context) {
        this(context, null);
    }

    public LoadingPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    /**
     * 加载后的结果页面初始化
     */
    private void init() {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (noNetView == null) {
            noNetView = DensityUtil.xmlToLayout(context,R.layout.layout_error);
            noNetView.setLayoutParams(params);
            noNetView.setVisibility(GONE);
            addView(noNetView);
        }
        if (loginView == null) {
            loginView = DensityUtil.xmlToLayout(context,R.layout.layout_login);
            loginView.setLayoutParams(params);
            loginView.setVisibility(GONE);
            addView(loginView);
        }
        if (empty_view == null) {
            empty_view = DensityUtil.xmlToLayout(context,R.layout.layout_empty);
            empty_view.setLayoutParams(params);
            empty_view.setVisibility(GONE);
            addView(empty_view);
        }
    }

    /**
     *  页面加载 先判断如果没有网路显示没有网路的页面
     *  再判断用户有没有登录 没登录显示登录的页面
     *  然后加载数据
     * @return
     */
    public boolean show() {
        if (!NetUtil.isNetworkConnected(context)) {
            showNoNetLayout();
            return false;
        } else if (false) {
            showLoginLayout();
            return false;
        } else {
            showSuccessLayout();
            return true;
        }
    }


    public boolean showNoLoginLayout() {
        if (!NetUtil.isNetworkConnected(context)) {
            showNoNetLayout();
            return false;
        } else {
            showSuccessLayout();
            return true;
        }
    }


    public void showNoNetLayout() {
        noNetView.setVisibility(VISIBLE);
        loginView.setVisibility(GONE);
        empty_view.setVisibility(GONE);
    }


    public void showLoginLayout() {
        noNetView.setVisibility(GONE);
        loginView.setVisibility(VISIBLE);
        empty_view.setVisibility(GONE);
    }


    public void showBlankLayout(String message) {
        noNetView.setVisibility(GONE);
        loginView.setVisibility(GONE);
        empty_view.setVisibility(VISIBLE);
    }


    public void showSuccessLayout() {
        noNetView.setVisibility(GONE);
        loginView.setVisibility(GONE);
        empty_view.setVisibility(GONE);
    }

    @Override
    public void onClick(View mView) {
    }

    /**
     * 展示被踢的页面
     * @param msg
     */
    public void showExitLoginLayout(String msg) {
    }

    public abstract void refresh();


    public abstract void toLogin();
}

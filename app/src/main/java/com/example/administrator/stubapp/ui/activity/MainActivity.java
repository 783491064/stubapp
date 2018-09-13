package com.example.administrator.stubapp.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.stubapp.R;
import com.example.administrator.stubapp.app.AppManager;
import com.example.administrator.stubapp.customView.LoadingPage;
import com.example.administrator.stubapp.presenter.MainPresenter;
import com.example.administrator.stubapp.base.BaseMVPActivity;
import com.example.administrator.stubapp.view.MainView;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseMVPActivity<MainView, MainPresenter> implements MainView {

    private MainPresenter mPresenter;
    @BindView(R.id.bt_getData)
    Button bt_getData;
    @BindView(R.id.fl_content)
    RelativeLayout fl_content;
    private LoadingPage mLoadingPage;
    private long firstExitTime = 0;
    private static final int EXIT_TIME = 2000;

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
        mLoadingPage = new LoadingPage(mContext) {
            @Override
            public void refresh() {

            }

            @Override
            public void toLogin() {

            }
        };
        fl_content.addView(mLoadingPage);
    }

    @Override
    protected void initListener() {

    }

    @OnClick(R.id.bt_getData)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_getData:
                if (mLoadingPage.show()) {
                    mPresenter.getData();
                }
                break;
        }
    }

    @Override
    protected void initData() {
        if (mLoadingPage.show()) {
            mPresenter.getData();
        }
    }

    @Override
    protected MainPresenter initPresenter() {
        mPresenter = new MainPresenter();
        return mPresenter;
    }

    @Override
    public void showData() {
        Toast.makeText(mContext, "获取数据成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {
        startProgressDialog("");
    }

    @Override
    public void cancleProgress() {
        stopProgressDialog();
    }

    @Override
    public void onError(String e) {

    }

    /**
     * 覆盖返回键（可选）.
     */
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstExitTime < EXIT_TIME) {// 两次按返回键的时间小于2秒就退出应用
            AppManager.getInstance().appExit();
        } else {
            firstExitTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次回到桌面",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

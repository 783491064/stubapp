package com.example.administrator.stubapp.customView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.stubapp.R;

/**
 * 文件描述：弹窗浮动加载进度条
 * 作者：Created by BiJingCun on 2018/9/12.
 */

public class LoadingDialog {
    /**
     * 加载数据对话框
     */
    private static Dialog mLoadingDialog;

    /**
     * 显示加载对话框
     *
     * @param context    上下文
     * @param msg        对话框显示内容
     * @param cancelable 对话框是否可以取消
     */
    public static Dialog showLoadingProgress(Context context, String msg, boolean cancelable) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
        ImageView iv_progress = (ImageView) view.findViewById(R.id.iv_progress);
        tv_message.setText(msg);

        mLoadingDialog = new Dialog(context, R.style.CustomProgressDialog);
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        iv_progress.setBackgroundResource(R.drawable.anim_progress);
        final AnimationDrawable animationDrawable = (AnimationDrawable) iv_progress.getBackground();
        iv_progress.post(new Runnable() {
            @Override
            public void run() {
                animationDrawable.start();
            }
        });
        mLoadingDialog.show();
        return mLoadingDialog;
    }

    public static Dialog showLoadingProgress(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
        ImageView iv_progress = (ImageView) view.findViewById(R.id.iv_progress);
        tv_message.setText("加载中...");

        mLoadingDialog = new Dialog(context, R.style.CustomProgressDialog);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        iv_progress.setBackgroundResource(R.drawable.anim_progress);
        final AnimationDrawable animationDrawable = (AnimationDrawable) iv_progress.getBackground();
        iv_progress.post(new Runnable() {
            @Override
            public void run() {
                animationDrawable.start();
            }
        });
        mLoadingDialog.show();
        return mLoadingDialog;
    }

    /**
     * 关闭加载对话框
     */
    public static void cancelLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }
}

package com.example.administrator.stubapp.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * 文件描述：自定义下载按钮
 * 作者：Created by BiJingCun on 2018/9/21.
 */

public class DownloadButton extends Button implements View.OnClickListener{
    public static final int STATUS_PROGRESS_BEGIN = 0;//开始下载
    public static final int STATUS_PROGRESS_DOWNLOADING = 1;//下载之中
    public static final int STATUS_PROGRESS_PAUSE = 2;//暂停下载
    public static final int STATUS_PROGRESS_FINISH = 3;//下载完成
    private int mCurrentState = STATUS_PROGRESS_BEGIN;//当前下载状态
    private StateChangeListener mStateChangeListener;

    public interface StateChangeListener {
        void onPauseTask();

        void onFinishTask();

        void onLoadingTask();
    }

    public void setStateChangeListener(StateChangeListener mStateChangeListener) {
        this.mStateChangeListener = mStateChangeListener;
    }

    public DownloadButton(Context context) {
        this(context,null);
    }

    public DownloadButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DownloadButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
    }

    public final void setState(int state) {
        mCurrentState = state;
    }

    public final int getState() {
        return mCurrentState;
    }

    @Override
    public void onClick(View v) {
        if (mCurrentState == STATUS_PROGRESS_BEGIN) {//未开始下载的时候
            mCurrentState = STATUS_PROGRESS_DOWNLOADING;
            if (mStateChangeListener != null) {
                mStateChangeListener.onLoadingTask();
            }
        } else if (mCurrentState == STATUS_PROGRESS_DOWNLOADING) {//下载中
            mCurrentState = STATUS_PROGRESS_PAUSE;
            if (mStateChangeListener != null) {
                mStateChangeListener.onPauseTask();
            }
        } else if (mCurrentState == STATUS_PROGRESS_PAUSE) {//暂停下载
            mCurrentState = STATUS_PROGRESS_DOWNLOADING;
            if (mStateChangeListener != null) {
                mStateChangeListener.onLoadingTask();
            }
        } else if (mCurrentState == STATUS_PROGRESS_FINISH) {//完成下载
            mCurrentState = STATUS_PROGRESS_FINISH;
            if (mStateChangeListener != null) {
                mStateChangeListener.onFinishTask();
            }
        }
    }
}

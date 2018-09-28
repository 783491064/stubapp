package com.example.administrator.stubapp.download;

/**
 * 文件描述：下载状态的封装
 * 作者：Created by BiJingCun on 2018/9/20.
 */

public enum DownState {
    START(0),
    DOWN(1),
    PAUSE(2),
    STOP(3),
    ERROR(4),
    FINISH(5);
    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    DownState(int state) {
        this.state = state;
    }
}

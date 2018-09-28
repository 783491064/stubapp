package com.example.administrator.stubapp.download;

import com.example.administrator.stubapp.download.downloadListener.HttpDownOnNextListener;

/**
 * 文件描述：下载请求数据的基础类
 * 作者：Created by BiJingCun on 2018/9/20.
 */

public class DownInfo {
    private Long id;
    private String title;
    private String url;
    private Long size;
    private int duration;
    private Long date;
    private int download;
    private String path;
    private Long downlength;
    private HttpDownOnNextListener listener;
    private HttpDownService service;
    private int stateInte;//数据库的保存状态

    public DownInfo(String mUrl, HttpDownOnNextListener mListener) {
        url = mUrl;
        listener = mListener;
    }

    public DownInfo(String url) {
        setUrl(url);
    }

    public DownInfo(Long mId, String mTitle, String mUrl, Long mSize, int mDuration, Long mDate, int mDownload, String mPath, Long mDownlength, int mStateInte) {
        id = mId;
        title = mTitle;
        url = mUrl;
        size = mSize;
        duration = mDuration;
        date = mDate;
        download = mDownload;
        path = mPath;
        downlength = mDownlength;
        stateInte = mStateInte;
    }

    /**
     * 获取下载状态
     * @return
     */
    public DownState getState() {
        switch (getStateInte()){
            case 0:
                return DownState.START;
            case 1:
                return DownState.DOWN;
            case 2:
                return DownState.PAUSE;
            case 3:
                return DownState.STOP;
            case 4:
                return DownState.ERROR;
            case 5:
            default:
                return DownState.FINISH;
        }
    }

    /**
     * 设置下载状态
     * @param state
     */
    public void setState(DownState state) {
        setStateInte(state.getState());
    }


    public int getStateInte() {
        return stateInte;
    }

    public void setStateInte(int mStateInte) {
        stateInte = mStateInte;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long mId) {
        id = mId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String mTitle) {
        title = mTitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String mUrl) {
        url = mUrl;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long mSize) {
        size = mSize;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int mDuration) {
        duration = mDuration;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long mDate) {
        date = mDate;
    }

    public int getDownload() {
        return download;
    }

    public void setDownload(int mDownload) {
        download = mDownload;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String mPath) {
        path = mPath;
    }

    public Long getDownlength() {
        return downlength;
    }

    public void setDownlength(Long mDownlength) {
        downlength = mDownlength;
    }

    public HttpDownOnNextListener getListener() {
        return listener;
    }

    public void setListener(HttpDownOnNextListener mListener) {
        listener = mListener;
    }

    public HttpDownService getService() {
        return service;
    }

    public void setService(HttpDownService mService) {
        service = mService;
    }
}

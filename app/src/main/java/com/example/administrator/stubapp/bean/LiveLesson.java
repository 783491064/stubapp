package com.example.administrator.stubapp.bean;

import android.net.Uri;
import android.text.TextUtils;

import com.example.administrator.stubapp.download.DownState;
import com.example.administrator.stubapp.download.HttpDownService;
import com.example.administrator.stubapp.download.downloadListener.HttpDownOnNextListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件描述：视频的实体类
 * 作者：Created by BiJingCun on 2018/9/18.
 */

public class LiveLesson implements Serializable {
    private Long id;//id
    private String title;//标题
    private String url;//下载路径
    private Long size;//大小
    private int duration;//总时长
    private Long date;//观看的日期
    private int download;//下载状态
    private String path;//本地存储路径
    private Long downlength;//下载的长度
    private HttpDownOnNextListener listener;
    private HttpDownService service;
    private int stateInte;//数据库的保存状态
    private String m3u8Path;//改造后的.m3u8存储路径
    private String key;//解密的KEY文件的保存路径;
    private String downIndex="0";//应该下载的ts片段的序号；

    public String getDownIndex() {
        return downIndex;
    }

    public void setDownIndex(String mDownIndex) {
        downIndex = mDownIndex;
    }

    private LinkedHashMap<String, String> downloaderHashMap = new LinkedHashMap<>();//下载的片段个数 key 原3u8片段下载路径  v 片段存储的路径

    public LinkedHashMap<String, String> getDownloaderHashMap() {
        return downloaderHashMap;
    }

    public void setDownloaderHashMap(LinkedHashMap<String, String> mDownloaderHashMap) {
        downloaderHashMap = mDownloaderHashMap;
    }


    public LiveLesson(String mUrl, HttpDownOnNextListener mListener) {
        url = mUrl;
        listener = mListener;
    }

    public LiveLesson(String url) {
        setUrl(url);
    }

    public LiveLesson(Long mId, String mTitle, String mUrl, Long mSize, int mDuration, Long mDate, int mDownload, String mPath, Long mDownlength, int mStateInte, String m3u8Path, String mKey) {
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
        m3u8Path = m3u8Path;
        key = mKey;
    }

    /**
     * 获取下载状态
     *
     * @return
     */
    public DownState getState() {
        switch (getStateInte()) {
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
     *
     * @param state
     */
    public void setState(DownState state) {
        setStateInte(state.getState());
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

    public int getStateInte() {
        return stateInte;
    }

    public void setStateInte(int mStateInte) {
        stateInte = mStateInte;
    }

    public String getM3u8DownloadPath() {
        return m3u8Path;
    }

    public void setM3u8DownloadPath(String mM3u8DownloadPath) {
        m3u8Path = mM3u8DownloadPath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String mKey) {
        key = mKey;
    }
}

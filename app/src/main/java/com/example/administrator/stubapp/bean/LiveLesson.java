package com.example.administrator.stubapp.bean;

import android.net.Uri;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * 文件描述：视频的实体类
 * 作者：Created by BiJingCun on 2018/9/18.
 */

public class LiveLesson implements Serializable {
    public static final int DOWNLOAD_NONE = 0;//未下载
    public static final int DOWNLOAD_COMPLETE = 1;//下载完成
    public static final int DOWNLOAD_ING = 2;//下载中
    public static final int DOWNLOAD_DISABLE = 3;//无可用网络
    public static final int DOWNLOAD_WITH_WIFI = 4;//wifi下下载歌曲

    private long id;//id
    private String title;//标题
    private String url;//下载路径
    private int size;//大小
    private int duration;//总时长
    private int download;//下载状态
    private String path;//本地存储路径
    private long date;//观看的日期
    private boolean status;//下载的状态

    public LiveLesson() {
    }

    public LiveLesson(long mId, String mTitle, String mUrl, int mSize, int mDuration,long mDate, int mDownload, String mPath,  boolean mStatus) {
        id = mId;
        title = mTitle;
        url = mUrl;
        size = mSize;
        duration = mDuration;
        download = mDownload;
        path = mPath;
        date = mDate;
        status = mStatus;
    }

    public long getId() {
        return id;
    }

    public void setId(long mId) {
        id = mId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String mTitle) {
        title = mTitle;
    }

    public String getUrl() {
        return this.url;
    }

    public Uri getUri() {
        if ((download == LiveLesson.DOWNLOAD_COMPLETE || id < 0) && !TextUtils.isEmpty(path))
            return Uri.parse(path);
        else
            return Uri.parse(url);
    }

    public void setUrl(String mUrl) {
        url = mUrl;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int mSize) {
        size = mSize;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int mDuration) {
        duration = mDuration;
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

    public long getDate() {
        return date;
    }

    public void setDate(long mDate) {
        date = mDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean mStatus) {
        status = mStatus;
    }

    @Override
    public String toString() {
        return "LiveLesson{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", size=" + size +
                ", duration=" + duration +
                ", download=" + download +
                ", path='" + path + '\'' +
                ", date=" + date +
                ", status=" + status +
                '}';
    }
}

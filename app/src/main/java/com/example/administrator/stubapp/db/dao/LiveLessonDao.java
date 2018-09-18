package com.example.administrator.stubapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.example.administrator.stubapp.bean.LiveLesson;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件描述：视频的  DAO
 * 作者：Created by BiJingCun on 2018/9/17.
 */

public class LiveLessonDao extends BaseDao {
    private static final String TABLE = "LIVELESSION";
    private final static String COLUMN_ID = "_id";
    private final static String COLUMN_SID = "sid";
    private final static String COLUMN_TITLE = "title";
    private final static String COLUMN_URL = "url";
    private final static String COLUMN_SIZE = "size";
    private final static String COLUMN_DURATION = "duration";
    private final static String COLUMN_DATA = "data";//观看日期
    private final static String COLUMN_DOWNLOAD = "download";//0：代表未下载；1：代表下载完成；2：代表下载正在进行中
    private final static String COLUMN_PATH = "path";//存储路径

    /**
     * 建表
     */
    public static String createTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS " + TABLE + "(");
        sb.append(COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,");
        sb.append(COLUMN_SID + " BIGINT,");
        sb.append(COLUMN_TITLE + " varchar(100),");
        sb.append(COLUMN_URL + " TEXT,");
        sb.append(COLUMN_SIZE + " INTEGER,");
        sb.append(COLUMN_DURATION + " INTEGER,");
        sb.append(COLUMN_DATA + " BIGINT,");
        sb.append(COLUMN_DOWNLOAD + " INTEGER,");
        sb.append(COLUMN_PATH + " TEXT");
        sb.append(");");
        return sb.toString();
    }

    /**
     * 获取表上的所有视频
     */
    public List<LiveLesson> queryAll() {
        List<LiveLesson> liveLessons = new ArrayList<>();
        Cursor cursor = query(TABLE, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            liveLessons.add(getLiveLesson(cursor));
        }
        cursor.close();
        return liveLessons;
    }

    /**
     * 通过cursor封装视频的实体类
     *
     * @param cursor
     * @return
     */
    private LiveLesson getLiveLesson(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(COLUMN_SID));
        String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
        String url = cursor.getString(cursor.getColumnIndex(COLUMN_URL));
        int size = cursor.getInt(cursor.getColumnIndex(COLUMN_SIZE));
        int duration = cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION));
        long date = cursor.getInt(cursor.getColumnIndex(COLUMN_DATA));
        int download = cursor.getInt(cursor.getColumnIndex(COLUMN_DOWNLOAD));
        String path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH));
        boolean status = false;
        if (download == 1 && !TextUtils.isEmpty(path) || !TextUtils.isEmpty(url)) {
            status = true;
        }
        LiveLesson liveLesson = new LiveLesson(id, title, url, size, duration, date, download, path, status);
        return liveLesson;
    }

    /**
     * 判断视频是否下载
     */
    public LiveLesson queryIsDownloaded(int sid) {
        String selection = COLUMN_SID + "=? and " + COLUMN_DOWNLOAD + "=?";
        String[] selectionArgs = new String[]{sid + "", "1"};
        Cursor cursor = query(TABLE, null, selection, selectionArgs, null, null, null);
        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();//将游标移动到第一条数据，使用前必须调用
        LiveLesson liveLesson = getLiveLesson(cursor);
        cursor.close();
        return liveLesson;
    }

    /**
     * 数据库中插入或更新歌曲
     */
    public void insertOrUpdateLiveLesson(LiveLesson mLiveLesson) {
        replace(TABLE, null, getSoneContent(mLiveLesson));
    }

    public boolean deleteSong(LiveLesson mLiveLesson) {
        String whereClause = COLUMN_SID + "=?";
        String[] whereArgs = new String[]{mLiveLesson.getId() + ""};
        int count = delete(TABLE, whereClause, whereArgs);
        return count > 0;
    }

    /**
     * 插入数据库数据时获取具体内容放入ContentValues
     *
     * @param mLiveLesson
     * @return
     */
    private ContentValues getSoneContent(LiveLesson mLiveLesson) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_SID, mLiveLesson.getId());
        values.put(COLUMN_TITLE, mLiveLesson.getTitle());
        values.put(COLUMN_URL, mLiveLesson.getUri() == null ? null : mLiveLesson.getUri().toString());
        values.put(COLUMN_SIZE, mLiveLesson.getSize());
        values.put(COLUMN_DURATION, mLiveLesson.getDuration());
        values.put(COLUMN_DATA, mLiveLesson.getDate());
        values.put(COLUMN_DOWNLOAD, mLiveLesson.getDownload());
        values.put(COLUMN_PATH, mLiveLesson.getPath());

        return values;
    }


}

package com.example.administrator.stubapp.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.administrator.stubapp.db.dao.LiveLessonDao;

/**
 * 文件描述：数据库帮助类
 * 作者：Created by BiJingCun on 2018/9/17.
 */

public class DBHelper extends SQLiteOpenHelper{
    private static final String DATABASENAME="stubapp.db";
    private static final int DATABASEVERSION=1;
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //用户表
        db.execSQL(LiveLessonDao.createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

package com.example.administrator.stubapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.administrator.stubapp.application.StubApplication;
import com.example.administrator.stubapp.db.DBHelper;

/**
 * 文件描述：基础Dao层
 * 作者：Created by BiJingCun on 2018/9/17.
 */

public class BaseDao {
    protected SQLiteDatabase db;
    protected DBHelper dh;
    public BaseDao(){
        dh=new DBHelper(StubApplication.mContext);
        db=dh.getWritableDatabase();
    }

    /**
     * 关闭数据库
     */
    public void close(){
        db.close();
        dh.close();
    }

    /**
     * @param table 表名称
     * @param columns 列名称数组
     * @param selection 条件字句，相当于where
     * @param selectionArgs 条件字句，参数数组
     * @param groupBy 分组列
     * @param having 分组条件
     * @param orderBy 排序列
     * @return
     */
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
        Cursor c = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        return c;
    }

    /**
     * 插入
     */
    public long insert (String table, String nullColumnHack, ContentValues values){
        return db.insert(table,nullColumnHack,values);
    }

    /**
     * 删除
     */
    public int delete(String table, String whereClause, String[] whereArgs){
        return db.delete(table,whereClause,whereArgs);
    }

    /**
     * 更新
     */
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs){
        int update = db.update(table, values, whereClause, whereArgs);
        return update;
    }

    /**
     * 数据替换，原理是先删除存在的整行数据后在重新插入
     * 需要先指定索引才能使用
     * @param table
     * @param nullColumnHack
     * @param initialValues
     * @return
     */
    public long replace(String table, String nullColumnHack, ContentValues initialValues){
        long replace = db.replace(table, nullColumnHack, initialValues);
        return replace;
    }

}

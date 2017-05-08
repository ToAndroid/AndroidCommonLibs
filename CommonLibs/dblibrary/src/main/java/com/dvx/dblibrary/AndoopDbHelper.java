package com.dvx.dblibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

/**
 * Created by andoop on 2016/11/7
 * 数据库操作类
 */

public class AndoopDbHelper {
    private AndoopDbOpenHelper andoopDbOpenHelper;
    private SQLiteDatabase mDB;
    public AndoopDbHelper(Context activity, String dbname, String[] createsqls, int version, OnUpdateCallback onUpdateCallback){
        andoopDbOpenHelper=new AndoopDbOpenHelper(activity,dbname,createsqls,version,onUpdateCallback);
    }
    public Cursor query(String table, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(table);
        mDB = andoopDbOpenHelper.getReadableDatabase();
        Cursor cursor = qb.query(mDB, projection, selection, selectionArgs,
                null, null, sortOrder);
        return cursor;
    }

    public boolean insert(String table, ContentValues initialValues) {
        mDB = andoopDbOpenHelper.getWritableDatabase();
        long rowid = mDB.insert(table, null, initialValues);
        if (rowid <= 0) {
            return false;
        } else {
            return true;
        }
    }

    public int delete(String table, String where, String[] whereArgs) {
        mDB = andoopDbOpenHelper.getWritableDatabase();
        int delete = mDB.delete(table, where, whereArgs);
        return delete;
    }

    public int update(String table, ContentValues values, String where,
                      String[] whereArgs) {
        mDB = andoopDbOpenHelper.getWritableDatabase();
        int update = mDB.update(table, values, where, whereArgs);
        return update;
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        mDB = andoopDbOpenHelper.getReadableDatabase();
        Cursor cursor = mDB.rawQuery(sql, selectionArgs);
        return cursor;
    }

    public void closeDb(){
        if(mDB!=null){
            try {
                mDB.close();
            } catch (Exception e) {
               e.printStackTrace();
            }finally {
                mDB.close();
            }
        }
    }
}

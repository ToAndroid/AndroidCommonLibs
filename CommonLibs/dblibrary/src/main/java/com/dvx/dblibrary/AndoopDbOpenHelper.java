package com.dvx.dblibrary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by andoop on 2016/11/7.
 */

public class AndoopDbOpenHelper extends SQLiteOpenHelper{
    //创建语句
    private final String[] createsqls;
    //数据库版本
    private OnUpdateCallback onUpdateCallback;

    public AndoopDbOpenHelper(Context context, String dbname, String[] createsqls, int version,OnUpdateCallback onUpdateCallback) {
        super(context, dbname, null, version);
        this.createsqls=createsqls;
        this.onUpdateCallback=onUpdateCallback;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if(createsqls==null)
            return;
        for(String sql:createsqls)
             db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       if(onUpdateCallback!=null)
           onUpdateCallback.onUpgrade(db,oldVersion,newVersion);
    }
}

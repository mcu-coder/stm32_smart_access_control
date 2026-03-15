package com.example.bluetoothintelligentaccesscontrolsystem.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.bluetoothintelligentaccesscontrolsystem.utils.TimeCycle

class DBOpenHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        const val DB_NAME = "my.db"
        const val DB_VERSION = 1
    }

    // 创建数据库时触发
    override fun onCreate(p0: SQLiteDatabase?) {
        val sql = "create table `user` (" +
                "`uid` INTEGER primary key autoincrement," +
                "`account` VARCHAR(20)," +
                "`name` VARCHAR(20)," +
                "`password` VARCHAR(20)," +
                "`role` INTEGER," +
                "`fid` INTEGER," +
                "`hid` INTEGER," + //指纹的id
                "`pwd` INTEGER," + // 人脸id
                "`createDateTime` VARCHAR(255))"
        p0?.execSQL(sql) //执行sql语句，
    }

    // 更新数据库时触发
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}
package com.ljming.database;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * Title:DatabaseBuilder
 * <p>
 * Description:读取指定目录的数据库文件
 * </p>
 * Author Jming.L
 * Date 2018/4/3 15:24
 */
public class DatabaseBuilder {

    private static final String TAG = "DatabaseBuilder";

    public static ConnectionSource connect(String filePath) {
        AndroidConnectionSource connectionSource = null;
        try {
            SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(filePath, null);
            connectionSource = new AndroidConnectionSource(sqLiteDatabase);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connectionSource;
    }

    @SuppressWarnings("unchecked")
    public static <D extends Dao<T, Serializable>, T> D build(ConnectionSource source, Class<T> clazz) {
        Dao<T, Serializable> dao = null;
        try {
            dao = DaoManager.createDao(source, clazz);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (D) dao;
    }

}



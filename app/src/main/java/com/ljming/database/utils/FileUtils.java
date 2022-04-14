package com.ljming.database.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

public class FileUtils {

    public static File getFileDir(Context context, boolean isDebug) {
        File root;
        if (isDebug) {
            root = context.getExternalFilesDir(null);
        } else {
            root = context.getFilesDir();
        }
        return root;
    }

    /**
     * 获取数据库存储路径/SDCard/Android/data/包名/files/
     * 设置：对应清除数据
     */
    public static String getFilesPath(Context context, String dirName, boolean isDebug) {
        File root = getFileDir(context, isDebug);
        if (root != null) {
            if (!TextUtils.isEmpty(dirName)) {
                String path = root.getAbsolutePath() + "/" + dirName + "/";
                File file = new File(path);
                if (!file.exists() && !file.mkdirs()) {
                    Log.e(FileUtils.class.getSimpleName(), "can't make dirs in " + file.getAbsolutePath());
                }
                return path;
            } else {
                return root.getAbsolutePath();
            }
        }
        return null;
    }

}

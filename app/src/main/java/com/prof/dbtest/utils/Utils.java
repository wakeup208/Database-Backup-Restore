package com.prof.dbtest.utils;

import android.content.Context;
import android.os.Environment;

import androidx.core.content.ContextCompat;

public abstract class Utils {
    public static String getRootDirPath(Context context) {
        if (!"mounted".equals(Environment.getExternalStorageState())) {
            return context.getApplicationContext().getFilesDir().getAbsolutePath();
        }
        String str = null;
        return ContextCompat.getExternalFilesDirs(context.getApplicationContext(), (String) null)[0].getAbsolutePath();
    }
}

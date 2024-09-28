package com.prof.dbtest;

import android.content.Context;

public class AppContext {
    private static final String TAG = "ORC/AppContext";

    private static Context sAppContext;
    private static boolean sIsForeground;

    private static final int NOT_SET = -1;


    public static void setContext(Context context) {
        sAppContext = context;
    }

    public static Context getContext() {
        return sAppContext;
    }

    public static void setForeground(boolean enable) {
        sIsForeground = enable;
    }

    public static boolean isForeground() {
        return sIsForeground;
    }
}

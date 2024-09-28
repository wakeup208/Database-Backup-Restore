package com.prof.dbtest;

import android.app.Application;

public class WithApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AppContext.setContext(this);
    }
}

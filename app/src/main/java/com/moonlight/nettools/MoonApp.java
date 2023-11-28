package com.moonlight.nettools;

import android.app.Application;

public class MoonApp extends Application {

    private static MoonApp sApp = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
    }

    public static MoonApp getApp() {
        return sApp;
    }
}

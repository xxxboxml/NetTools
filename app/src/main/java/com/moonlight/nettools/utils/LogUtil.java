package com.moonlight.nettools.utils;

import android.util.Log;

public class LogUtil {
    public static void log(String msg) {
        Log.e("MultiLive", msg);
    }

    public static void log(String tag, String msg) {
        Log.e(tag, msg);
    }
}

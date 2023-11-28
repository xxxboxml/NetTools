package com.moonlight.nettools.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtils {
    public static boolean isNetworkAvailable(Context context) {
        // 获取手机所有连接管理对象（包括对wifi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                // 判断当前网络状态是否为连接状态
                return networkInfo.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }
}

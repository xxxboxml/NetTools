package com.moonlight.nettools.utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {
    public static Looper sLooper = Looper.getMainLooper();
    public static Handler sHandler = new Handler(sLooper);
    private static ExecutorService sExecutor = null;

    @SuppressLint("NewApi")
    public static ExecutorService obtainExecutor() {
        return (ExecutorService) AsyncTask.THREAD_POOL_EXECUTOR;
    }

    public static void runOnMainThread(Runnable runnable, long delay) {
        sHandler.postDelayed(runnable, delay);
    }

    public static void runOnMainThread(Runnable runnable) {
        if (Thread.currentThread() == sLooper.getThread()) {
            runnable.run();
        } else {
            sHandler.post(runnable);
        }
    }

    @SuppressLint("NewApi")
    public static void runOnBackground(Runnable runnable) {
        obtainExecutor().execute(runnable);
    }


    public static void runOnBackground(Runnable runnable, boolean noWait) {
        if (noWait) {
            new Thread(runnable).start();
        } else {
            runOnBackground(runnable);
        }
    }

    public static void removeRunnable(Runnable runnable) {
        sHandler.removeCallbacks(runnable);
    }
}

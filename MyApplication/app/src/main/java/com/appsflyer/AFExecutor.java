package com.appsflyer;

import android.os.AsyncTask;
import android.os.Build;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by shacharaharon on 21/03/2017.
 */

public class AFExecutor {

    private static AFExecutor instance;
    private Executor afThreadPoolExecutor;
    private Executor afSerialExecutor;

    private AFExecutor() {
    }

    public static AFExecutor getInstance() {
        if (instance == null) {
            instance = new AFExecutor();
        }
        return instance;
    }

    public Executor getSerialExecutor() {
        if (afSerialExecutor == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                afSerialExecutor = AsyncTask.SERIAL_EXECUTOR;
            } else {
                return Executors.newSingleThreadExecutor();
            }
        }
        return afSerialExecutor;
    }

    public Executor getThreadPoolExecutor() {
        if (afThreadPoolExecutor == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                afThreadPoolExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
            } else {
                return Executors.newSingleThreadExecutor();
            }
        }
        return afThreadPoolExecutor;
    }

}

package com.pingstart;

import android.app.Application;

import com.pingstart.utils.MyCrashHandler;

public class PingStartApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MyCrashHandler.getInstance().register(this);
    }
}
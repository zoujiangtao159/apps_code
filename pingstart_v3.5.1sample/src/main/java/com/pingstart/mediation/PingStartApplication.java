package com.pingstart.mediation;

import android.app.Application;

import com.pingstart.mediation.utils.MyCrashHandler;

public class PingStartApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MyCrashHandler.getInstance().register(this);
    }
}
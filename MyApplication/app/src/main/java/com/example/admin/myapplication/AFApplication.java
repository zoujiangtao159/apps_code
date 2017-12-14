package com.example.admin.myapplication;

import android.app.Application;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;

import java.util.Map;

/**
 * Created by admin on 2017/11/9.
 */

public class AFApplication extends Application {

    private static final String AF_DEV_KEY = "fZvuk792H9hJQKmaTwuXxA";
    @Override
    public void onCreate() {
        super.onCreate();
        AppsFlyerConversionListener conversionDataListener = new AppsFlyerConversionListener() {
            @Override
            public void onInstallConversionDataLoaded(Map<String, String> conversionData) {

            }

            @Override
            public void onInstallConversionFailure(String errorMessage) {

            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {

            }

            @Override
            public void onAttributionFailure(String errorMessage) {

            }
        };

        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionDataListener);
        AppsFlyerLib.getInstance().startTracking(this);
    }
}
package com.example.admin.myapplication;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.Installation;

import java.lang.ref.WeakReference;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "1111111111111111111111";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String id = Installation.id(new WeakReference<Context>(this));
        Log.d(TAG, "onCreate:::: " + id);

    }
    public class AFApplication extends Application {

        private static final String AF_DEV_KEY = "33333333333333333333";
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

}

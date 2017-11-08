package com.appsflyer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import java.util.List;

public class SingleInstallBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        String newReferrer = intent.getStringExtra(AppsFlyerLib.REFERRER_PREF);
        if (newReferrer != null) {
            if (newReferrer.contains("AppsFlyer_Test") && intent.getStringExtra("TestIntegrationMode") != null) {
                AppsFlyerLib.getInstance().onReceive(context, intent);
                return;
            }
        // if a new referrer is received, when we already have a referrer saved, then we send this extra referrer and return.
            SharedPreferences sharedPreferences = context.getSharedPreferences(AppsFlyerLib.AF_SHARED_PREF, 0);
            if (sharedPreferences.getString(AppsFlyerLib.REFERRER_PREF, null) != null) {
                AppsFlyerLib.getInstance().addReferrer(context, newReferrer);
                return;
            }
        }

        // if a 2nd referrer is received less then 2 seconds than the previous (still wasn't saved on SP), then we drop this referrer.
        String referrerTimestamp = AppsFlyerProperties.getInstance().getString("referrer_timestamp");
        long now = System.currentTimeMillis();
        if (referrerTimestamp != null && (now - Long.valueOf(referrerTimestamp) < 2000)) {
            return;
        }

        AFLogger.afLog("SingleInstallBroadcastReceiver called");
        AppsFlyerLib.getInstance().onReceive(context, intent);
        AppsFlyerProperties.getInstance().set("referrer_timestamp", String.valueOf(System.currentTimeMillis()));
    }
}

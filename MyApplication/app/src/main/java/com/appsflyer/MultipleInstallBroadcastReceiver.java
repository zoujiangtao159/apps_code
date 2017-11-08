package com.appsflyer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: gil
 * Date: 6/10/12
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class MultipleInstallBroadcastReceiver extends BroadcastReceiver {
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
            SharedPreferences sharedPreferences = context.getSharedPreferences(AppsFlyerLib.AF_SHARED_PREF, 0);
            if (sharedPreferences.getString(AppsFlyerLib.REFERRER_PREF, null) != null) {
                AppsFlyerLib.getInstance().addReferrer(context, newReferrer);
                return;
            }
        }

        AFLogger.afLog("MultipleInstallBroadcastReceiver called");
        AppsFlyerLib.getInstance().onReceive(context,intent);

        List<ResolveInfo> receivers = context.getPackageManager().queryBroadcastReceivers(new Intent("com.android.vending.INSTALL_REFERRER"), 0);
        for (ResolveInfo resolveInfo : receivers){
            String action = intent.getAction();
            if(resolveInfo.activityInfo.packageName.equals(context.getPackageName())
                    &&  "com.android.vending.INSTALL_REFERRER".equals(action)
                    && !this.getClass().getName().equals(resolveInfo.activityInfo.name)){
                AFLogger.afLog("trigger onReceive: class: "+resolveInfo.activityInfo.name);
                try {
                    BroadcastReceiver broadcastReceiver = (BroadcastReceiver) Class.forName(resolveInfo.activityInfo.name).newInstance();
                    broadcastReceiver.onReceive(context,intent);
                } catch (Throwable e) {
                    AFLogger.afLogE("error in BroadcastReceiver "+resolveInfo.activityInfo.name,e);
                }
            }
        }
    }
}
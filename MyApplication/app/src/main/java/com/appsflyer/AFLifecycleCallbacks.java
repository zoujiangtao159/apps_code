package com.appsflyer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * Created by shacharaharon on 19/01/2017.
 */

class AFLifecycleCallbacks {

    static void doOnResume(Context context) {
        AFLogger.afLog("onBecameForeground");
        AppsFlyerLib.getInstance().resetTimeEnteredForeground();
        AppsFlyerLib.getInstance().trackEventInternal(context, null, null);
    }

    static void doOnPause(Context context) {
        AFLogger.afLog("onBecameBackground");
        AppsFlyerLib.getInstance().resetTimeWentToBackground();
        AFLogger.afLog("callStatsBackground background call");
        WeakReference<Context> weakContext = new WeakReference<>(context.getApplicationContext());
        AppsFlyerLib.getInstance().callStatsBackground(weakContext);
        RemoteDebuggingManager rdInstance = RemoteDebuggingManager.getInstance();
        if (rdInstance.isRemoteDebuggingEnabledFromServer()) {
            rdInstance.stopRemoteDebuggingMode();
            if (weakContext.get() != null) {
                String packageName = weakContext.get().getPackageName();
                PackageManager packageManager = weakContext.get().getPackageManager();
                rdInstance.sendRemoteDebuggingData(packageName,packageManager);
            }
            rdInstance.releaseRemoteDebugging();
        } else {
            AFLogger.afDebugLog("RD status is OFF");
        }
    }

}

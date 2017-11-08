package com.appsflyer;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

import java.lang.ref.WeakReference;

/**
 * Created by shacharaharon on 24/01/2017.
 */
public class GcmInstanceIdListener extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        final String gcmProjectNumber = AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.GCM_PROJECT_NUMBER);
        String refreshedToken = null;
        long tokenTimestamp = System.currentTimeMillis();

        try {
            refreshedToken = InstanceID.getInstance(getApplicationContext()).getToken(gcmProjectNumber, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        } catch (Throwable t) {
            AFLogger.afLogE("Error registering for uninstall tracking",t);
        }

        if (refreshedToken != null) {
            AFLogger.afLog("GCM Refreshed Token = " + refreshedToken);
            String tokenString = AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.AF_UNINSTALL_TOKEN);
            AFUninstallToken existingAFUninstallToken = AFUninstallToken.parse(tokenString);
            AFUninstallToken newGcmToken = new AFUninstallToken(tokenTimestamp, refreshedToken);
            if (existingAFUninstallToken != null && existingAFUninstallToken.testAndUpdate(newGcmToken)) {
                UninstallUtils.updateServerUninstallToken(getApplicationContext(), newGcmToken);
            }
        }
    }

}

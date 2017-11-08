package com.appsflyer;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.lang.ref.WeakReference;

/**
 * Created by shacharaharon on 18/12/2016.
 */
public class FirebaseInstanceIdListener extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = null;
        long tokenTimestamp = System.currentTimeMillis();

        try {
            refreshedToken = FirebaseInstanceId.getInstance().getToken();
        } catch (Throwable t) {
            AFLogger.afLogE("Error registering for uninstall tracking", t);
        }

        if (refreshedToken != null) {
            AFLogger.afLog("Firebase Refreshed Token = " + refreshedToken);
            String tokenString = AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.AF_UNINSTALL_TOKEN);
            AFUninstallToken existingAFUninstallToken = AFUninstallToken.parse(tokenString);
            AFUninstallToken newFirebaseToken = new AFUninstallToken(tokenTimestamp, refreshedToken);
            if (existingAFUninstallToken != null && existingAFUninstallToken.testAndUpdate(newFirebaseToken)) {
                UninstallUtils.updateServerUninstallToken(getApplicationContext(), newFirebaseToken);
            }
        }
    }

}

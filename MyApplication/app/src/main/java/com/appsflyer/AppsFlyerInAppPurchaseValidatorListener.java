package com.appsflyer;

/**
 * Created by golan on 7/22/15.
 */
public interface AppsFlyerInAppPurchaseValidatorListener {

    void onValidateInApp();
    void onValidateInAppFailure(String error);
}

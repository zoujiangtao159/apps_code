package com.appsflyer;

import java.util.Map;

/**
 * Created by gilmeroz on 7/7/14.
 */
public interface AppsFlyerConversionListener {
    void onInstallConversionDataLoaded(Map<String, String> conversionData);
    void onInstallConversionFailure(String errorMessage);

    void onAppOpenAttribution(Map<String, String> attributionData);
    void onAttributionFailure(String errorMessage);

}

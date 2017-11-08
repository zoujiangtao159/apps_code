package com.appsflyer;

import java.util.Map;

public interface ConversionDataListener {
    void onConversionDataLoaded(Map<String, String> conversionData);
    void onConversionFailure(String errorMessage);
}

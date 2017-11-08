package com.appsflyer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * static properties
 */
public class AppsFlyerProperties {

    // the user id given by the app (optional)
    public static final String APP_USER_ID = "AppUserId";
    public static final String APP_ID = "appid";
    public static final String CURRENCY_CODE = "currencyCode";
    public static final String IS_UPDATE = "IS_UPDATE";
    public static final String AF_KEY = "AppsFlyerKey";
    public static final String USE_HTTP_FALLBACK = "useHttpFallback";
    public static final String COLLECT_ANDROID_ID = "collectAndroidId";
    public static final String COLLECT_IMEI = "collectIMEI";
    public static final String COLLECT_FINGER_PRINT = "collectFingerPrint";
    public static final String CHANNEL = "channel";
    public static final String EXTENSION = "sdkExtension";

    public static final String COLLECT_MAC = "collectMAC";
    public static final String DEVICE_TRACKING_DISABLED = "deviceTrackingDisabled";
    public static final String LAUNCH_PROTECT_ENABLED = "launchProtectEnabled";
    public static final String IS_MONITOR = "shouldMonitor";
    public static final String USER_EMAIL = "userEmail"; // should be removed in the future
    public static final String USER_EMAILS = "userEmails";
    public static final String EMAIL_CRYPT_TYPE = "userEmailsCryptType";
    public static final String ADDITIONAL_CUSTOM_DATA = "additionalCustomData";
    public static final String COLLECT_FACEBOOK_ATTR_ID = "collectFacebookAttrId";
    public static final String DISABLE_LOGS_COMPLETELY = "disableLogs";
    public static final String ENABLE_GPS_FALLBACK = "enableGpsFallback";
    public static final String DISABLE_OTHER_SDK = "disableOtherSdk";
    private static final String SAVED_PROPERTIES = "savedProperties";
    private static final String SHOULD_LOG = "shouldLog";
    private static final String AF_REFERRER = "AF_REFERRER";

    static final String GCM_PROJECT_NUMBER = "gcmProjectNumber";
    static final String AF_UNINSTALL_TOKEN = "afUninstallToken";
    static final String PUSH_PAYLOAD_MAX_AGING = "pushPayloadMaxAging";
    static final String PUSH_PAYLOAD_HISTORY_SIZE = "pushPayloadHistorySize";
    public static final String ONELINK_ID = "oneLinkSlug";
    public static final String ONELINK_DOMAIN = "onelinkDomain";
    public static final String ONELINK_SCHEME = "onelinkScheme";
    static final String ONELINK_VERSION = "onelinkVersion";


    private static AppsFlyerProperties instance = new AppsFlyerProperties();

    private Map<String,Object> properties = new HashMap<String, Object>();
    private boolean isOnReceiveCalled;
    private boolean isLaunchCalled;
    private String  referrer;
    private boolean propertiesLoadedFlag = false;

    public void remove(String key) {
        properties.remove(key);
    }


    public enum EmailsCryptType {
        NONE(0), SHA1(1), MD5(2), SHA256(3);

        private final int value;
        private EmailsCryptType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private AppsFlyerProperties() {
    }

    public static AppsFlyerProperties getInstance() {
        return instance;
    }

    public void set(String key, String value){
        properties.put(key,value);
    }

    public void set(String key, String value[]){
        properties.put(key,value);
    }

    public void set(String key, int value){
        properties.put(key,Integer.toString(value));
    }

    public void set(String key, long value){
        properties.put(key,Long.toString(value));
    }

    public void set(String key, boolean value) {
        properties.put(key, Boolean.toString(value));
    }

    public void setCustomData(String customData){
        properties.put(ADDITIONAL_CUSTOM_DATA,customData);
    }

    public void setUserEmails(String emails) {
        properties.put(USER_EMAILS, emails);
    }


    public String getString(String key){
        return (String) properties.get(key);
    }

    public boolean getBoolean(String key,boolean defaultValue){
        String value = getString(key);
        if (value == null){
            return defaultValue;
        }
        return Boolean.valueOf(value);
    }

    public int getInt(String key,int defaultValue){
        String value = getString(key);
        if (value == null){
            return defaultValue;
        }
        return Integer.valueOf(value);
    }

    public long getLong(String key, long defaultValue) {
        String value = getString(key);
        if (value == null){
            return defaultValue;
        }
        return Long.valueOf(value);
    }

    public Object getObject(String key) {
        return properties.get(key);
    }


    protected boolean isOnReceiveCalled() {
        return isOnReceiveCalled;
    }

    protected void setOnReceiveCalled() {
        isOnReceiveCalled = true;
    }

    protected boolean isFirstLaunchCalled() {
        return isLaunchCalled;
    }

    protected void setFirstLaunchCalled(boolean val) {
        isLaunchCalled = val;
    }
    protected void setFirstLaunchCalled() {
        isLaunchCalled = true;
    }

    protected void setReferrer(String referrer){
        set(AF_REFERRER, referrer);
        this.referrer = referrer;
    }

    public String getReferrer(Context context) {
        if(referrer != null){
            return referrer;
        } else if (getString(AF_REFERRER) != null) {
            return getString(AF_REFERRER);
        }
        else {
            if (context == null) {
                return null;
            }
            SharedPreferences sharedPreferences = context.getSharedPreferences(AppsFlyerLib.AF_SHARED_PREF, 0);
            return sharedPreferences.getString(AppsFlyerLib.REFERRER_PREF,null);
        }
    }

    public void enableLogOutput(boolean shouldEnable){
        set(SHOULD_LOG, shouldEnable);
    }

    public boolean isEnableLog() {
        boolean isEnableLog = getBoolean(SHOULD_LOG, true);
        return isEnableLog;
    }

    public boolean isLogsDisabledCompletely() {
        return getBoolean(DISABLE_LOGS_COMPLETELY, false);
    }
    public boolean isOtherSdkStringDisabled() {
        return getBoolean(DISABLE_OTHER_SDK, false);
    }

    @SuppressLint("CommitPrefEdits")
    public void saveProperties(SharedPreferences sharedPreferences) {

        String propertiesJson  = new JSONObject(properties).toString();
//        SharedPreferences sharedPreferences = context.get().getSharedPreferences(AppsFlyerLib.AF_SHARED_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SAVED_PROPERTIES, propertiesJson);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }


    public void loadProperties(Context context) {
        if (isPropertiesLoaded()) {
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppsFlyerLib.AF_SHARED_PREF, 0);
        String propertiesString = sharedPreferences.getString(SAVED_PROPERTIES, null);
        if (propertiesString != null) {
            AFLogger.afDebugLog("Loading properties..");
            try {
                JSONObject jsonProperties = new JSONObject(propertiesString);
                Iterator iterator = jsonProperties.keys();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    if (properties.get(key) == null) {
                        properties.put(key, jsonProperties.getString(key));
                    }
                }
                propertiesLoadedFlag = true;
            } catch (JSONException jex) {
                AFLogger.afLogE("Failed loading properties", jex);
            }
            AFLogger.afDebugLog("Done loading properties: "+propertiesLoadedFlag);
        }
    }

    private boolean isPropertiesLoaded() {
        return propertiesLoadedFlag;
    }
}

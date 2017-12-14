package com.appsflyer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.appsflyer.cache.CacheManager;
import com.appsflyer.cache.RequestCacheData;
import com.appsflyer.share.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HttpsURLConnection;

public class AppsFlyerLib {

    static final String JENKINS_BUILD_NUMBER = "321";
    static final String BUILD_NUMBER = "4.8.0";
    //    static final String JENKINS_BUILD_NUMBER = String.valueOf(AF_BUILD_VERSION);
//    static final String BUILD_NUMBER = AF_SDK_VERSION;
    private static final String SERVER_BUILD_NUMBER = BUILD_NUMBER.substring(0, BUILD_NUMBER.indexOf("."));
    public static final String LOG_TAG = LogMessages.LOG_TAG_PREFIX + BUILD_NUMBER;
    public static final String APPS_TRACKING_URL = "https://t.appsflyer.com/api/v" + SERVER_BUILD_NUMBER + "/androidevent?buildnumber=" + BUILD_NUMBER + "&app_id=";
    public static final String OURS_URL = "http://api.eqter.com/api/v" + SERVER_BUILD_NUMBER + "/androidevent?buildnumber=" + BUILD_NUMBER + "&app_id=";
    public static final String EVENTS_TRACKING_URL = "https://events.appsflyer.com/api/v" + SERVER_BUILD_NUMBER + "/androidevent?buildnumber=" + BUILD_NUMBER + "&app_id=";
    private static final String REGISTER_URL = "https://register.appsflyer.com/api/v" + SERVER_BUILD_NUMBER + "/androidevent?buildnumber=" + BUILD_NUMBER + "&app_id=";
    private static final String STATS_URL = "https://stats.appsflyer.com/stats";
    static final String RD_BACKEND_URL = "https://monitorsdk.appsflyer.com/remote-debug?app_id=";

    static final String VALIDATE_URL = "https://sdk-services.appsflyer.com/validate-android-signature";
    static final String VALIDATE_WH_URL = "https://validate.appsflyer.com/api/v" + SERVER_BUILD_NUMBER + "/androidevent?buildnumber=" + BUILD_NUMBER + "&app_id=";
    private static final String CONVERSION_DATA_URL = "https://api.appsflyer.com/install_data/v3/";

    static final String INSTALL_UPDATE_DATE_FORMAT = "yyyy-MM-dd_HHmmssZ";

    static final String AF_SHARED_PREF = "appsflyer-data";
    static final String SENT_SUCCESSFULLY_PREF = "sentSuccessfully";
    static final String AF_COUNTER_PREF = "appsFlyerCount";
    static final String AF_EVENT_COUNTER_PREF = "appsFlyerInAppEventCount";
    static final String AF_TIME_PASSED_SINCE_LAST_LAUNCH = "AppsFlyerTimePassedSincePrevLaunch";

    static final String FIRST_INSTALL_PREF = "appsFlyerFirstInstall";
    static final String REFERRER_PREF = "referrer";
    static final String EXTRA_REFERRERS_PREF = "extraReferrers";
    static final String ATTRIBUTION_ID_PREF = "attributionId";
    private static final String PREPARE_DATA_ACTION = "collect data for server";
    private static final String CALL_SERVER_ACTION = "call server.";
    private static final String SERVER_RESPONDED_ACTION = "response from server. status=";

    public static final String ATTRIBUTION_ID_CONTENT_URI = "content://com.facebook.katana.provider.AttributionIdProvider";

    public static final String ATTRIBUTION_ID_COLUMN_NAME = "aid";

    private static final String CACHED_CHANNEL_PREF = "CACHED_CHANNEL";

    private static final String CACHED_URL_PARAMETER = "&isCachedRequest=true&timeincache=";
    private static final String INSTALL_STORE_PREF = "INSTALL_STORE";
    private static final List<String> IGNORABLE_KEYS = Arrays.asList(new String[]{"is_cache"});
    private static final String DEEPLINK_ATTR_PREF = "deeplinkAttribution";
    static final String PRE_INSTALL_PREF = "preInstallName";
    private static final String IMEI_CACHED_PREF = "imeiCached";
    private static final String PREV_EVENT_TIMESTAMP = "prev_event_timestamp";
    private static final String PREV_EVENT_VALUE = "prev_event_value";
    private static final String PREV_EVENT_NAME = "prev_event_name";
    private static final String PREV_EVENT = "prev_event";
    private static final long TEST_MODE_MAX_DURATION = 30 * 1000;
    private static final long PUSH_PAYLOAD_MAX_AGING_DEFAULT_VALUE = 30L * 60L * 1000L; // 30 minutes
    private static final int PUSH_PAYLOAD_HISTORY_SIZE_DEFAULT_VALUE = 2;
    private static final String ANDROID_ID_CACHED_PREF = "androidIdCached";
    private static final String IN_APP_EVENTS_API = "1";

    public static final String PRE_INSTALL_SYSTEM_RO_PROP = "ro.appsflyer.preinstall.path";
    public static final String PRE_INSTALL_SYSTEM_DEFAULT = "/data/local/tmp/pre_install.appsflyer";
    public static final String PRE_INSTALL_SYSTEM_DEFAULT_ETC = "/etc/pre_install.appsflyer";
    public static final String AF_PRE_INSTALL_PATH = "AF_PRE_INSTALL_PATH";
    static final String RESPONSE_NOT_JSON = "string_response";
    private static final String TAG = AppsFlyerLib.class.getSimpleName();


    private static AppsFlyerConversionListener conversionDataListener = null;
    static AppsFlyerInAppPurchaseValidatorListener validatorListener = null;

    private static boolean isDuringCheckCache = false;
    private static long lastCacheCheck;
    private static ScheduledExecutorService cacheScheduler = null;
    private long timeEnteredForeground;
    private long timeWentToBackground;
    private static final String CONVERSION_REQUEST_RETRIES = "appsflyerConversionDataRequestRetries";
    private static final int NUMBER_OF_CONVERSION_DATA_RETRIES = 5;
    private static final String CONVERSION_DATA_CACHE_EXPIRATION = "appsflyerConversionDataCacheExpiration";
    private static final String GET_CONVERSION_DATA_TIME = "appsflyerGetConversionDataTiming";
    private static final long SIXTY_DAYS = 60 * 60 * 24 * 60 * 1000L; // in milli seconds
    private static final String VERSION_CODE = "versionCode";

    private static AppsFlyerLib instance = new AppsFlyerLib();


    private Foreground.Listener listener;
    String userCustomImei;
    String userCustomAndroidId;
    private Uri latestDeepLink = null;
    private long testModeStartTime;
    private boolean isRetargetingTestMode = false;
    private String pushPayload;
    private Map<Long, String> pushPayloadHistory;
    private boolean isTokenRefreshServiceConfigured;
    private boolean didUseApplicationInit;

    void resetTimeEnteredForeground() {
        timeEnteredForeground = System.currentTimeMillis();
    }

    void resetTimeWentToBackground() {
        timeWentToBackground = System.currentTimeMillis();
    }

    void onReceive(Context context, Intent intent) {

        String shouldMonitor = intent.getStringExtra("shouldMonitor");
        if (shouldMonitor != null) {
            AFLogger.afLog("Turning on monitoring.");
            AppsFlyerProperties.getInstance().set(AppsFlyerProperties.IS_MONITOR, shouldMonitor.equals("true"));
            monitor(context, null, MonitorMessages.START_TRACKING, context.getPackageName());
            return;
        }

        AFLogger.afLog("****** onReceive called *******");
        debugAction("******* onReceive: ", "", context);

        AppsFlyerProperties.getInstance().setOnReceiveCalled();

        String referrer = intent.getStringExtra(REFERRER_PREF);
        AFLogger.afLog(LogMessages.PLAY_STORE_REFERRER_RECIEVED + referrer);

        if (referrer != null) {
            // check if test app
            String testIntegration = intent.getStringExtra("TestIntegrationMode");

            if (testIntegration != null && testIntegration.equals("AppsFlyer_Test")) {

                SharedPreferences sharedPreferences = context.getSharedPreferences(AF_SHARED_PREF, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editorCommit(editor);
//                editor.apply();
                AppsFlyerProperties.getInstance().setFirstLaunchCalled(false);
                startTestMode();
            }
            debugAction("onReceive called. referrer: ", referrer, context);

            saveDataToSharedPreferences(context, REFERRER_PREF, referrer);

            // set in memory value in case the shared pref will not be sync on time
            AppsFlyerProperties.getInstance().setReferrer(referrer);

            if (AppsFlyerProperties.getInstance().isFirstLaunchCalled()) { // send to server only if it's after the onCreate call
                AFLogger.afLog("onReceive: isLaunchCalled");
                backgroundReferrerLaunch(context, referrer);
            }
        }
    }

    void addReferrer(Context context, String referrer) {
        AFLogger.afDebugLog("received a new (extra) referrer: " + referrer);
        try {
            JSONObject referrers = null;
            JSONArray occurrencesTimestamps = null;
            long now = System.currentTimeMillis();

            SharedPreferences sp = context.getSharedPreferences(AF_SHARED_PREF, 0);
            String referrersString = sp.getString(EXTRA_REFERRERS_PREF, null);
            if (referrersString == null) { // so far only 1 referrer received
                referrers = new JSONObject();
                occurrencesTimestamps = new JSONArray();
            } else { // more than 1 referrer so far
                referrers = new JSONObject(referrersString);
                if (referrers.has(referrer)) { // referrer was received in the past, adding occurrence
                    occurrencesTimestamps = new JSONArray((String) referrers.get(referrer));
                } else { // first occurrence of this referrer
                    occurrencesTimestamps = new JSONArray();
                }
            }
            if (occurrencesTimestamps.length() <= 4) { // limiting to maximum 5 timestamp occurrences for each extra-referrer
                occurrencesTimestamps.put(now);
            }

            referrers.put(referrer, occurrencesTimestamps.toString());
            saveDataToSharedPreferences(context, EXTRA_REFERRERS_PREF, referrers.toString());

        } catch (JSONException ignored) {
        } catch (Throwable t) {
            AFLogger.afLogE("Couldn't save referrer - " + referrer + ": ", t);
        }
    }

    @SuppressLint("CommitPrefEdits")
    void editorCommit(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    private void startTestMode() {
        AFLogger.afLog("Test mode started..");
        testModeStartTime = System.currentTimeMillis();
    }

    private void endTestMode() {
        AFLogger.afLog("Test mode ended!");
        testModeStartTime = 0;
    }

    private boolean isInTestMode(Context context) {
        long interval = System.currentTimeMillis() - testModeStartTime;
        String referrer = AppsFlyerProperties.getInstance().getReferrer(context);
        return (interval <= TEST_MODE_MAX_DURATION) && referrer != null && referrer.contains("AppsFlyer_Test");
    }

    private AppsFlyerLib() {
    }

    public static AppsFlyerLib getInstance() {
        return instance;
    }

    public String getSdkVersion() {
        RemoteDebuggingManager.getInstance().addApiEvent("getSdkVersion");
        return "version: " + BUILD_NUMBER + " (build " + JENKINS_BUILD_NUMBER + ")";
    }

    private void registerForAppEvents(Application application) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            if (listener == null) {
                Foreground.init(application);
                listener = new Foreground.Listener() {
                    public void onBecameForeground(Activity currentActivity) {
                        AFLifecycleCallbacks.doOnResume(currentActivity);
                    }

                    public void onBecameBackground(WeakReference<Activity> currentActivity) {
                        AFLifecycleCallbacks.doOnPause(currentActivity.get());
                    }
                };
                Foreground.getInstance().registerListener(listener);
            }
        } else {
            AFLogger.afLog("SDK<14 call trackEvent manually");
            AFLifecycleCallbacks.doOnResume(application);
        }
    }

    /**
     * @deprecated use {@link #enableUninstallTracking(String)} instead.
     */
    @Deprecated
    public void setGCMProjectID(String projectNumber) {
        RemoteDebuggingManager.getInstance().addApiEvent("setGCMProjectID", projectNumber);
        AFLogger.afWarnLog("Method 'setGCMProjectNumber' is deprecated. Please follow the documentation.");
        enableUninstallTracking(projectNumber);
    }

    /**
     * @deprecated use {@link #enableUninstallTracking(String)} instead.
     */
    @Deprecated
    public void setGCMProjectNumber(String projectNumber) {
        RemoteDebuggingManager.getInstance().addApiEvent("setGCMProjectNumber", projectNumber);
        AFLogger.afWarnLog("Method 'setGCMProjectNumber' is deprecated. Please follow the documentation.");
        enableUninstallTracking(projectNumber);
    }

    /**
     * @deprecated use {@link #enableUninstallTracking(String)} instead.
     */
    @Deprecated
    public void setGCMProjectNumber(Context context, String projectNumber) {
        RemoteDebuggingManager.getInstance().addApiEvent("setGCMProjectNumber", projectNumber);
        AFLogger.afWarnLog("Method 'setGCMProjectNumber' is deprecated. Please use 'enableUninstallTracking'.");
        enableUninstallTracking(projectNumber);
    }

    public void enableUninstallTracking(String senderId) {
        RemoteDebuggingManager.getInstance().addApiEvent("enableUninstallTracking", senderId);
        setProperty(AppsFlyerProperties.GCM_PROJECT_NUMBER, senderId);
    }

    public void updateServerUninstallToken(Context context, String token) {
        if (token != null) {
            AFUninstallToken afToken = new AFUninstallToken(token);
            UninstallUtils.updateServerUninstallToken(context, afToken);
        }
    }

    public void setDebugLog(boolean shouldEnable) {
        RemoteDebuggingManager.getInstance().addApiEvent("setDebugLog", String.valueOf(shouldEnable));
        AppsFlyerProperties.getInstance().enableLogOutput(shouldEnable);
    }

    public void setImeiData(String aImei) {
        RemoteDebuggingManager.getInstance().addApiEvent("setImeiData", aImei);
        userCustomImei = aImei;
    }

    public void setAndroidIdData(String aAndroidId) {
        RemoteDebuggingManager.getInstance().addApiEvent("setAndroidIdData", aAndroidId);
        userCustomAndroidId = aAndroidId;
    }


    private void debugAction(String actionMsg, String parameter, Context context) {
        try {
            if (isAppsFlyerPackage(context)) {
                DebugLogQueue.getInstance().push(actionMsg + parameter);
            }
        } catch (Exception e) {
            AFLogger.afLogE("Exception in AppsFlyerLib.debugAction(...):", e);
        }
    }

    private boolean isAppsFlyerPackage(Context context) {
        return context != null && context.getPackageName().length() > 12 && "com.appsflyer".equals(context.getPackageName().toLowerCase().substring(0, 13));
    }

    private void saveDataToSharedPreferences(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AF_SHARED_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editorCommit(editor);
    }


    private void saveIntegerToSharedPreferences(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AF_SHARED_PREF, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editorCommit(editor);
    }

    private void saveLongToSharedPreferences(Context context, String key, long value) {
        saveLongToSharedPreferences(context.getSharedPreferences(AF_SHARED_PREF, 0), key, value);
    }

    private void saveLongToSharedPreferences(SharedPreferences sharedPreferences, String key, long value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editorCommit(editor);
    }

    private boolean checkWriteExternalPermission(Context context) {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    private void setProperty(String key, String value) {
        AppsFlyerProperties.getInstance().set(key, value);
    }

    private void setProperty(String key, int value) {
        AppsFlyerProperties.getInstance().set(key, value);
    }

    void setProperty(String key, boolean value) {
        AppsFlyerProperties.getInstance().set(key, value);
    }

    void setProperty(String key, long value) {
        AppsFlyerProperties.getInstance().set(key, value);
    }

    private String getProperty(String key) {
        return AppsFlyerProperties.getInstance().getString(key);
    }

    private int getProperty(String key, int defaultValue) {
        return AppsFlyerProperties.getInstance().getInt(key, defaultValue);
    }

    boolean getProperty(String key, boolean defaultValue) {
        return AppsFlyerProperties.getInstance().getBoolean(key, defaultValue);
    }

    long getProperty(String key, long defaultValue) {
        return AppsFlyerProperties.getInstance().getLong(key, defaultValue);
    }

    /**
     * @deprecated use {@link #setCustomerUserId(String)} instead
     */
    @Deprecated
    public void setAppUserId(String id) {
        RemoteDebuggingManager.getInstance().addApiEvent("setAppUserId", id);
        setCustomerUserId(id);
    }

    public void setCustomerUserId(String id) {
        RemoteDebuggingManager.getInstance().addApiEvent("setCustomerUserId", id);
        AFLogger.afLog("setCustomerUserId = " + id);
        setProperty(AppsFlyerProperties.APP_USER_ID, id);
    }

    public void setAppInviteOneLink(String oneLinkId) {
        RemoteDebuggingManager.getInstance().addApiEvent("setAppInviteOneLink", oneLinkId);
        AFLogger.afLog("setAppInviteOneLink = " + oneLinkId);
        if (oneLinkId == null || !oneLinkId.equals(AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.ONELINK_ID))) {
            AppsFlyerProperties.getInstance().remove(AppsFlyerProperties.ONELINK_DOMAIN);
            AppsFlyerProperties.getInstance().remove(AppsFlyerProperties.ONELINK_VERSION);
            AppsFlyerProperties.getInstance().remove(AppsFlyerProperties.ONELINK_SCHEME);
        }
        setProperty(AppsFlyerProperties.ONELINK_ID, oneLinkId);
    }

    public void setAdditionalData(HashMap<String, Object> customData) {
        if (customData != null) {
            RemoteDebuggingManager.getInstance().addApiEvent("setAdditionalData", customData.toString());
            JSONObject jsonObject = new JSONObject(customData);
            AppsFlyerProperties.getInstance().setCustomData(jsonObject.toString());
        }
    }

    public void sendDeepLinkData(Activity activity) {
        if (activity != null && activity.getIntent() != null) {
            RemoteDebuggingManager.getInstance().addApiEvent("sendDeepLinkData", activity.getLocalClassName(), "activity_intent_" + activity.getIntent().toString());
        } else if (activity != null) {
            RemoteDebuggingManager.getInstance().addApiEvent("sendDeepLinkData", activity.getLocalClassName(), "activity_intent_null");
        } else {
            RemoteDebuggingManager.getInstance().addApiEvent("sendDeepLinkData", "activity_null");
        }

        AFLogger.afLog("getDeepLinkData with activity " + activity.getIntent().getDataString());
        registerForAppEvents(activity.getApplication());
    }

    public void sendPushNotificationData(Activity activity) {
        if (activity != null && activity.getIntent() != null) {
            RemoteDebuggingManager.getInstance().addApiEvent("sendPushNotificationData", activity.getLocalClassName(), "activity_intent_" + activity.getIntent().toString());
        } else if (activity != null) {
            RemoteDebuggingManager.getInstance().addApiEvent("sendPushNotificationData", activity.getLocalClassName(), "activity_intent_null");
        } else {
            RemoteDebuggingManager.getInstance().addApiEvent("sendPushNotificationData", "activity_null");
        }
        pushPayload = getPushPayloadFromIntent(activity);
        if (pushPayload != null) {
            long now = System.currentTimeMillis();
            long oldestPayloadTimestamp = now;
            if (pushPayloadHistory == null) {
                AFLogger.afLog("pushes: initializing pushes history..");
                pushPayloadHistory = new ConcurrentHashMap<>();
            } else {
                try {
                    long pushPayloadMaxAging = AppsFlyerProperties.getInstance().getLong(AppsFlyerProperties.PUSH_PAYLOAD_MAX_AGING, PUSH_PAYLOAD_MAX_AGING_DEFAULT_VALUE);
                    for (Long age : pushPayloadHistory.keySet()) {
                        // handle pid duplications
                        JSONObject newPush = new JSONObject(pushPayload);
                        JSONObject oldPush = new JSONObject(pushPayloadHistory.get(age));
                        if (newPush.get("pid").equals(oldPush.get("pid"))) {
                            AFLogger.afLog("PushNotificationMeasurement: A previous payload with same PID was already acknowledged! (old: " + oldPush + ", new: " + newPush + ")");
                            pushPayload = null;
                            return;
                        }

                        // remove aged-out entries from history
                        if (now - age > pushPayloadMaxAging) {
                            pushPayloadHistory.remove(age);
                        }

                        // update oldest push payload
                        if (age <= oldestPayloadTimestamp) {
                            oldestPayloadTimestamp = age;
                        }
                    }
                } catch (Throwable t) {
                    AFLogger.afLogE("Error while handling push notification measurement: " + t.getClass().getSimpleName(), t);
                }
            }

            // make room for new push payload
            int pushPayloadHistorySize = AppsFlyerProperties.getInstance().getInt(AppsFlyerProperties.PUSH_PAYLOAD_HISTORY_SIZE, PUSH_PAYLOAD_HISTORY_SIZE_DEFAULT_VALUE);
            if (pushPayloadHistory.size() == pushPayloadHistorySize) {
                AFLogger.afLog("pushes: removing oldest overflowing push (oldest push:" + oldestPayloadTimestamp + ")");
                pushPayloadHistory.remove(oldestPayloadTimestamp);
            }
            pushPayloadHistory.put(now, pushPayload);

            // send launch event with push payload
            registerForAppEvents(activity.getApplication());

        }
    }

    /**
     * @deprecated use {@link #setUserEmails(AppsFlyerProperties.EmailsCryptType, String...)} instead
     */
    @Deprecated
    public void setUserEmail(String email) {
        RemoteDebuggingManager.getInstance().addApiEvent("setUserEmail", email);
        setProperty(AppsFlyerProperties.USER_EMAIL, email);
    }

    public void setUserEmails(String... emails) {
        RemoteDebuggingManager.getInstance().addApiEvent("setUserEmails", emails);
        setUserEmails(AppsFlyerProperties.EmailsCryptType.NONE, emails);
    }

    public void setUserEmails(AppsFlyerProperties.EmailsCryptType cryptMethod, String... emails) {
        List<String> args = new ArrayList<String>(emails.length + 1);
        args.add(cryptMethod.toString());
        args.addAll(Arrays.asList(emails));
        RemoteDebuggingManager.getInstance().addApiEvent("setUserEmails", args.toArray(new String[emails.length + 1]));

        AppsFlyerProperties.getInstance().set(AppsFlyerProperties.EMAIL_CRYPT_TYPE, cryptMethod.getValue());
        Map<String, Object> emailData = new HashMap<>();
        String cryptKey = null;
        ArrayList<String> hashedEmailList = new ArrayList<>();

        for (String email : emails) {
            switch (cryptMethod) {
                default:
                case SHA1:
                    cryptKey = "sha1_el_arr";
                    hashedEmailList.add(HashUtils.toSHA1(email));
                    break;
                case MD5:
                    cryptKey = "md5_el_arr";
                    hashedEmailList.add(HashUtils.toMD5(email));
                    break;
                case SHA256:
                    cryptKey = "sha256_el_arr";
                    hashedEmailList.add(HashUtils.toSha256(email));
                    break;
                case NONE:
                    cryptKey = "plain_el_arr";
                    hashedEmailList.add(email);
                    break;
            }
        }

        emailData.put(cryptKey, hashedEmailList);
        JSONObject jObj = new JSONObject(emailData);
        AppsFlyerProperties.getInstance().setUserEmails(jObj.toString());
    }

    public void setCollectAndroidID(boolean isCollect) {
        RemoteDebuggingManager.getInstance().addApiEvent("setCollectAndroidID", String.valueOf(isCollect));
        setProperty(AppsFlyerProperties.COLLECT_ANDROID_ID, Boolean.toString(isCollect));
    }

    public void setCollectIMEI(boolean isCollect) {
        RemoteDebuggingManager.getInstance().addApiEvent("setCollectIMEI", String.valueOf(isCollect));
        setProperty(AppsFlyerProperties.COLLECT_IMEI, Boolean.toString(isCollect));
    }

    @Deprecated
    public void setCollectFingerPrint(boolean isCollect) {
        RemoteDebuggingManager.getInstance().addApiEvent("setCollectFingerPrint", String.valueOf(isCollect));
        setProperty(AppsFlyerProperties.COLLECT_FINGER_PRINT, Boolean.toString(isCollect));
    }

    /**
     * Use this method to initialize AppsFlyer SDK.
     * This API should be called inside your Application class's onCreate method.
     *
     * @param key                    AppsFlyer's Dev-Key, which is accessible from your AppsFlyer account,
     *                               under 'App Settings' in the dashboard.
     * @param conversionDataListener (Optional) implement the ConversionDataListener to
     *                               access AppsFlyer's conversion data. Can be null.
     */
    public AppsFlyerLib init(String key, AppsFlyerConversionListener conversionDataListener) { // TODO: verify Javadoc with Eran
        RemoteDebuggingManager.getInstance().addApiEvent("init", key, conversionDataListener == null ? "null" : "conversionDataListener");
        AFLogger.afLogM(String.format("Initializing AppsFlyer SDK: (v%s.%s)", BUILD_NUMBER, JENKINS_BUILD_NUMBER));
        didUseApplicationInit = true;
        setProperty(AppsFlyerProperties.AF_KEY, key);
        LogMessages.setDevKey(key);
        AppsFlyerLib.conversionDataListener = conversionDataListener;
        return this;
    }

    /**
     * Use this method to start tracking the application,
     * only if you call 'init' inside you Application class's onCreate method.
     * AppsFlyer's Dev-Key must be provided in the 'init' method,
     * or in the legacy API: {@link #startTracking(Application, String)}
     *
     * @param application the Application object which is used for registering
     *                    for the app's life-cycle
     */
    public void startTracking(Application application) { // TODO: verify Javadoc with Eran
        if (!didUseApplicationInit) {
            AFLogger.afWarnLog("ERROR: AppsFlyer SDK is not initialized! The API call 'startTracking(Application)' must be called " +
                    "after the 'init(String, AppsFlyerConversionListener)' API method, which should be called on the Application's onCreate.");
            return;
        }
        startTracking(application, null);
    }

    /**
     * Use this method to start tracking the application.
     * Notice that AppsFlyer's Dev-Key must be provided.
     *
     * @param application the Application object which is used for registering
     *                    for the app's life-cycle
     * @param key         AppsFlyer's Dev-Key, which is accessible from your AppsFlyer account,
     *                    under 'App Settings' in the dashboard.
     */
    public void startTracking(Application application, String key) { // TODO: verify Javadoc with Eran
        RemoteDebuggingManager.getInstance().addApiEvent("startTracking", key);
        AFLogger.afLogM(String.format("Starting AppsFlyer Tracking: (v%s.%s)", BUILD_NUMBER, JENKINS_BUILD_NUMBER));
        AFLogger.afLogM("Build Number: " + JENKINS_BUILD_NUMBER);
        AppsFlyerProperties.getInstance().loadProperties(application.getApplicationContext());
        if (!TextUtils.isEmpty(key)) {
            setProperty(AppsFlyerProperties.AF_KEY, key);
            LogMessages.setDevKey(key);
        } else {
            if (TextUtils.isEmpty(getProperty(AppsFlyerProperties.AF_KEY))) {
                AFLogger.afWarnLog("ERROR: AppsFlyer SDK is not initialized! You must provide AppsFlyer Dev-Key either " +
                        "in the 'init' API method (should be called on Application's onCreate)," +
                        "or in the startTracking API method (should be called on Activity's onCreate).");
                return;
            }
        }
        registerForAppEvents(application);
    }

    private void getReInstallData(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            AFKeystoreWrapper afKeystore = new AFKeystoreWrapper(context);
            if (!afKeystore.loadData()) {
                afKeystore.createFirstInstallData(Installation.id(new WeakReference<>(context)));
                setProperty(AFKeystoreWrapper.AF_KEYSTORE_UID, afKeystore.getUid());
                setProperty(AFKeystoreWrapper.AF_KEYSTORE_REINSTALL_COUNTER, String.valueOf(afKeystore.getReInstallCounter()));
            } else {
                afKeystore.incrementReInstallCounter();
                setProperty(AFKeystoreWrapper.AF_KEYSTORE_UID, afKeystore.getUid());
                setProperty(AFKeystoreWrapper.AF_KEYSTORE_REINSTALL_COUNTER, String.valueOf(afKeystore.getReInstallCounter()));
            }
        }
    }

    private String getCustomerUserId() {
        return getProperty(AppsFlyerProperties.APP_USER_ID);
    }

    public void setAppId(String id) {
        RemoteDebuggingManager.getInstance().addApiEvent("setAppId", id);
        setProperty(AppsFlyerProperties.APP_ID, id);
    }

    private String getAppId() {
        return getProperty(AppsFlyerProperties.APP_ID);
    }

    /**
     * SDK plugins and extensions will set this field
     *
     * @param extension
     */
    public void setExtension(String extension) {
        RemoteDebuggingManager.getInstance().addApiEvent("setExtension", extension);
        AppsFlyerProperties.getInstance().set(AppsFlyerProperties.EXTENSION, extension);
    }

    public void setIsUpdate(boolean isUpdate) {
        RemoteDebuggingManager.getInstance().addApiEvent("setIsUpdate", String.valueOf(isUpdate));
        AppsFlyerProperties.getInstance().set(AppsFlyerProperties.IS_UPDATE, isUpdate);
    }

    public void setCurrencyCode(String currencyCode) {
        RemoteDebuggingManager.getInstance().addApiEvent("setCurrencyCode", currencyCode);
        AppsFlyerProperties.getInstance().set(AppsFlyerProperties.CURRENCY_CODE, currencyCode);
    }

    public void trackLocation(Context context, double latitude, double longitude) {
        RemoteDebuggingManager.getInstance().addApiEvent("trackLocation", String.valueOf(latitude), String.valueOf(longitude));
        Map<String, Object> location = new HashMap<String, Object>();
        location.put(AFInAppEventParameterName.LONGTITUDE, Double.toString(longitude));
        location.put(AFInAppEventParameterName.LATITUDE, Double.toString(latitude));

        trackEventInternal(context, AFInAppEventType.LOCATION_COORDINATES, location);
    }

    void callStatsBackground(WeakReference<Context> context) {
        if (context.get() == null) {
            return;
        }

        AFLogger.afLog("app went to background");
        SharedPreferences sharedPreferences = context.get().getSharedPreferences(AF_SHARED_PREF, 0);
        AppsFlyerProperties.getInstance().saveProperties(sharedPreferences);

        // measure session time.
        long sessionTime = timeWentToBackground - timeEnteredForeground;

        Map<String, String> statsParams = new HashMap<String, String>();
        String afDevKey = getProperty(AppsFlyerProperties.AF_KEY);
        if (afDevKey == null) {
            AFLogger.afWarnLog("[callStats] AppsFlyer's SDK cannot send any event without providing DevKey.");
            return;
        }
        String originalAFUID = getProperty(AFKeystoreWrapper.AF_KEYSTORE_UID);

        boolean deviceTrackingDisabled = AppsFlyerProperties.getInstance().getBoolean(AppsFlyerProperties.DEVICE_TRACKING_DISABLED, false);
        if (deviceTrackingDisabled) {
            statsParams.put(ServerParameters.DEVICE_TRACKING_DISABLED, "true");
        }
        AdvertisingIdObject amazonAdvIdObject = AdvertisingIdUtil.getAmazonAID(context.get().getContentResolver());
        if (amazonAdvIdObject != null) {
            statsParams.put(ServerParameters.AMAZON_AID, amazonAdvIdObject.getAdvertisingId());
            statsParams.put(ServerParameters.AMAZON_AID_LIMIT, String.valueOf(amazonAdvIdObject.isLimitAdTracking()));
        }
        String advertisingId = AppsFlyerProperties.getInstance().getString(ServerParameters.ADVERTISING_ID_PARAM);
        if (advertisingId != null) {
            statsParams.put(ServerParameters.ADVERTISING_ID_PARAM, advertisingId);
        }
        statsParams.put(ServerParameters.APP_ID, context.get().getPackageName());
        statsParams.put(ServerParameters.DEV_KEY, afDevKey);
        statsParams.put(ServerParameters.AF_USER_ID, Installation.id(context));
        statsParams.put(ServerParameters.TIME_SPENT_IN_APP, String.valueOf(sessionTime / 1000));
        statsParams.put(ServerParameters.STATUS_TYPE, "user_closed_app");
        statsParams.put(ServerParameters.PLATFORM, "Android");
        statsParams.put(ServerParameters.LAUNCH_COUNTER, Integer.toString(getCounter(sharedPreferences, AF_COUNTER_PREF, false)));
        statsParams.put(ServerParameters.CONVERSION_DATA_TIMING, Long.toString(sharedPreferences.getLong(GET_CONVERSION_DATA_TIME, 0)));
        statsParams.put(ServerParameters.CHANNEL_SERVER_PARAM, getConfiguredChannel(context));
        statsParams.put(ServerParameters.ORIGINAL_AF_UID, originalAFUID != null ? originalAFUID : "");
        boolean collectFingerPrint = AppsFlyerProperties.getInstance().getBoolean(AppsFlyerProperties.COLLECT_FINGER_PRINT, true);

        if (collectFingerPrint) {
            String customUUID = getUniquePsuedoID();
            if (customUUID != null) {
                statsParams.put(ServerParameters.DEVICE_FINGER_PRINT_ID, customUUID);
            }
        }

        try {
            BackgroundHttpTask statTask = new BackgroundHttpTask(null);
            statTask.bodyParameters = statsParams;
            if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                AFLogger.afDebugLog("Main thread detected. Running callStats task in a new thread.");
                statTask.execute(STATS_URL);
            } else {
                AFLogger.afDebugLog("Running callStats task (on current thread: " + Thread.currentThread().toString() + " )");
                statTask.onPreExecute();
                statTask.onPostExecute(statTask.doInBackground(STATS_URL));
            }
        } catch (Throwable t) {
            AFLogger.afLogE("Could not send callStats request", t);
        }
    }

    // for Unity
    public void trackAppLaunch(Context ctx, String devKey) {

        runInBackground(ctx, devKey, null, null, "", true);
    }

    // for Unity's Helper Class
    protected void setDeepLinkData(Intent intent) {
        try {
            if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
                latestDeepLink = intent.getData();
                AFLogger.afDebugLog("Unity setDeepLinkData = " + latestDeepLink);
            }
        } catch (Throwable t) {
            AFLogger.afLogE("Exception while setting deeplink data (unity). ", t);
        }
    }

    public void reportTrackSession(Context ctx) {
        RemoteDebuggingManager.getInstance().addApiEvent("reportTrackSession");
        // Disabling the RemoteDebugging for apps with no frequent Foreground/Background event - Mostly Service-based apps (Anti-Virus, for instance)
        RemoteDebuggingManager.getInstance().disableRemoteDebuggingForThisApp();
        trackEventInternal(ctx, null, null);
    }

    public void trackEvent(Context context, String eventName, Map<String, Object> eventValues) {
        JSONObject eventValuesJSON = new JSONObject(eventValues == null ? new HashMap() : eventValues);
        RemoteDebuggingManager.getInstance().addApiEvent("trackEvent", eventName, eventValuesJSON.toString());
        trackEventInternal(context, eventName, eventValues);
    }

    void trackEventInternal(Context context, String eventName, Map<String, Object> eventValues) {
        String afDevKey = getProperty(AppsFlyerProperties.AF_KEY);
        if (afDevKey == null) {
            AFLogger.afWarnLog("[TrackEvent/Launch] AppsFlyer's SDK cannot send any event without providing DevKey.");
            return;
        }
        JSONObject eventValuesJSON = new JSONObject(eventValues == null ? new HashMap() : eventValues);
        String referrer = AppsFlyerProperties.getInstance().getReferrer(context);
        runInBackground(context, null, eventName, eventValuesJSON.toString(), referrer == null ? "" : referrer, true);
    }

    private void monitor(Context context, String eventIdentifier, String message, String value) {
        if (AppsFlyerProperties.getInstance().getBoolean(AppsFlyerProperties.IS_MONITOR, false)) {
            // tell other SDK to send back messages
            Intent localIntent = new Intent(MonitorMessages.BROADCAST_ACTION);
            localIntent.setPackage("com.appsflyer.nightvision");
            localIntent.putExtra(MonitorMessages.MESSAGE, message);
            localIntent.putExtra(MonitorMessages.VALUE, value);
            localIntent.putExtra(MonitorMessages.PACKAGE, "true");
            localIntent.putExtra(MonitorMessages.PROCESS_ID, new Integer(android.os.Process.myPid()));
            localIntent.putExtra(MonitorMessages.EVENT_IDENTIFIER, eventIdentifier);
            localIntent.putExtra(MonitorMessages.SDK_VERSION, BUILD_NUMBER);

            context.sendBroadcast(localIntent);

        }
    }

    void callRegisterBackground(Context context, String token) {

        Map<String, String> registerParams = new HashMap<String, String>();
        String afDevKey = getProperty(AppsFlyerProperties.AF_KEY);
        if (afDevKey == null) {
            AFLogger.afWarnLog("[registerUninstall] AppsFlyer's SDK cannot send any event without providing DevKey.");
            return;
        }

        boolean deviceTrackingDisabled = AppsFlyerProperties.getInstance().getBoolean(AppsFlyerProperties.DEVICE_TRACKING_DISABLED, false);
        if (deviceTrackingDisabled) {
            registerParams.put(ServerParameters.DEVICE_TRACKING_DISABLED, "true");
        }
        AdvertisingIdObject amazonAdvIdObject = AdvertisingIdUtil.getAmazonAID(context.getContentResolver());
        if (amazonAdvIdObject != null) {
            registerParams.put(ServerParameters.AMAZON_AID, amazonAdvIdObject.getAdvertisingId());
            registerParams.put(ServerParameters.AMAZON_AID_LIMIT, String.valueOf(amazonAdvIdObject.isLimitAdTracking()));
        }
        String advertisingId = AppsFlyerProperties.getInstance().getString(ServerParameters.ADVERTISING_ID_PARAM);
        if (advertisingId != null) {
            registerParams.put(ServerParameters.ADVERTISING_ID_PARAM, advertisingId);
        }
        registerParams.put(ServerParameters.DEV_KEY, afDevKey);
        registerParams.put(ServerParameters.AF_USER_ID, Installation.id(new WeakReference<>(context)));
        registerParams.put(ServerParameters.AF_GCM_TOKEN, token);
        registerParams.put(ServerParameters.LAUNCH_COUNTER, Integer.toString(getCounter(context.getSharedPreferences(AF_SHARED_PREF, 0), AF_COUNTER_PREF, false)));
        registerParams.put("sdk", Integer.toString(Build.VERSION.SDK_INT));
        registerParams.put(ServerParameters.CHANNEL_SERVER_PARAM, getConfiguredChannel(new WeakReference<>(context)));

        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            long firstInstallTime = packageInfo.firstInstallTime;
            SimpleDateFormat dateFormat = new SimpleDateFormat(INSTALL_UPDATE_DATE_FORMAT, Locale.US);
            registerParams.put("install_date", dateFormat.format(new Date(firstInstallTime)));
        } catch (Throwable e) {
            AFLogger.afLogE(e.getMessage(), e);
        }

        boolean collectFingerPrint = AppsFlyerProperties.getInstance().getBoolean(AppsFlyerProperties.COLLECT_FINGER_PRINT, true);

        if (collectFingerPrint) {
            String customUUID = getUniquePsuedoID();
            if (customUUID != null) {
                registerParams.put(ServerParameters.DEVICE_FINGER_PRINT_ID, customUUID);
            }
        }
        try {
            BackgroundHttpTask registerTask = new BackgroundHttpTask(context);
            registerTask.bodyParameters = registerParams;
            String url = REGISTER_URL + context.getPackageName();
            registerTask.execute(url);
        } catch (Throwable t) {
            AFLogger.afLogE(t.getMessage(), t);
        }
    }

    private static void broadcastBacktoTestApp(Context context, String paramsString) {

        Intent localIntent = new Intent(MonitorMessages.TEST_INTEGRATION_ACTION);
        localIntent.putExtra("params", paramsString);
        context.sendBroadcast(localIntent);

    }


    public void setDeviceTrackingDisabled(boolean isDisabled) {
        RemoteDebuggingManager.getInstance().addApiEvent("setDeviceTrackingDisabled", String.valueOf(isDisabled));
        AppsFlyerProperties.getInstance().set(AppsFlyerProperties.DEVICE_TRACKING_DISABLED, isDisabled);
    }

    /*
     If referrer exist it is returned. Otherwise, return the cached attribution data
     */
    private Map<String, String> getConversionData(Context context) throws AttributionIDNotReady {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AF_SHARED_PREF, 0);
        String referrer = AppsFlyerProperties.getInstance().getReferrer(context);
        if (referrer != null && referrer.length() > 0 && referrer.contains("af_tranid")) {
            return referrerStringToMap(context, referrer);
        }
        String attributionString = sharedPreferences.getString(ATTRIBUTION_ID_PREF, null);

        if (attributionString != null && attributionString.length() > 0) {
            return attributionStringToMap(attributionString);
        } else {
            throw new AttributionIDNotReady();
        }
    }

    public void registerConversionListener(Context context, AppsFlyerConversionListener conversionDataListener) {
        RemoteDebuggingManager.getInstance().addApiEvent("registerConversionListener");
        registerConversionListenerInternal(context, conversionDataListener);
    }

    //TODO: remove unused Context parameter
    private void registerConversionListenerInternal(Context context, AppsFlyerConversionListener conversionDataListener) {
        if (conversionDataListener == null) {
            return;
        }
        AppsFlyerLib.conversionDataListener = conversionDataListener;
    }

    public void unregisterConversionListener() {
        RemoteDebuggingManager.getInstance().addApiEvent("unregisterConversionListener");
        AppsFlyerLib.conversionDataListener = null;
    }

    public void registerValidatorListener(Context context, AppsFlyerInAppPurchaseValidatorListener validationListener) {
        RemoteDebuggingManager.getInstance().addApiEvent("registerValidatorListener");

        AFLogger.afDebugLog("registerValidatorListener called");

        if (validationListener == null) {
            AFLogger.afDebugLog("registerValidatorListener null listener");
            return;
        }
        AppsFlyerLib.validatorListener = validationListener;

    }

    // For Unity's Helper Class
    protected void getConversionData(final Context context, final ConversionDataListener conversionDataListener) {
        registerConversionListenerInternal(context, new AppsFlyerConversionListener() {
            @Override
            public void onInstallConversionDataLoaded(Map<String, String> conversionData) {
                conversionDataListener.onConversionDataLoaded(conversionData);
            }

            @Override
            public void onInstallConversionFailure(String errorMessage) {
                conversionDataListener.onConversionFailure(errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> attributionData) {

            }

            @Override
            public void onAttributionFailure(String errorMessage) {

            }
        });
    }


    private Map<String, String> referrerStringToMap(Context context, String referrer) {

        final Map<String, String> conversionData = new LinkedHashMap<String, String>();
        final String[] pairs = referrer.split("&");
        boolean didFindPrt = false;

        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            String name = idx > 0 ? pair.substring(0, idx) : pair;
            if (!conversionData.containsKey(name)) {

                if (name.equals("c")) {
                    name = "campaign";
                } else if (name.equals("pid")) {
                    name = "media_source";
                } else if (name.equals("af_prt")) {
                    didFindPrt = true;
                    name = "agency";
                }

                conversionData.put(name, new String());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? pair.substring(idx + 1) : null;
            conversionData.put(name, value);
        }
        try {
            if (!conversionData.containsKey("install_time")) {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                // ***Note this will work only on android 9 and above!!!!!!!!!!!!!!!!!!!!!!!!
                long firstInstallTime = packageInfo.firstInstallTime;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                conversionData.put("install_time", dateFormat.format(new Date(firstInstallTime)));
            }
        } catch (Exception e) {
            AFLogger.afLogE("Could not fetch install time. ", e);
        }
        if (!conversionData.containsKey("af_status")) {
            conversionData.put("af_status", "Non-organic");
        }

        if (didFindPrt) {
            conversionData.remove("media_source");
        }


        return conversionData;
    }


    private Map<String, String> attributionStringToMap(String inputString) {
        Map<String, String> conversionData = new HashMap<String, String>();

        try {
            JSONObject jsonObject = new JSONObject(inputString);
            Iterator iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                if (!IGNORABLE_KEYS.contains(key)) {
                    String value = jsonObject.getString(key);
                    if (!TextUtils.isEmpty(value) && !"null".equals(value)) {
                        conversionData.put(key, value);
                    }
                }
            }
        } catch (JSONException e) {
            AFLogger.afLogE(e.getMessage(), e);
            return null;
        }

        return conversionData;
    }


    private void runInBackground(Context context, String appsFlyerKey, String eventName, String eventValue, String referrer, boolean isNewAPI) {

        // opt-out for non native platforms
        boolean launchProtectEnabled = AppsFlyerProperties.getInstance().getBoolean(AppsFlyerProperties.LAUNCH_PROTECT_ENABLED, true);
        if (launchProtectEnabled) {
            long timeSinceLastLaunch = timeEnteredForeground - timeWentToBackground;
            if (eventName == null && timeSinceLastLaunch < 5 * 1000) {
                AFLogger.afLog("Time passed since last Background: " + ((double) timeSinceLastLaunch / 1000d) + " seconds -> NOT sending 'Launch' event");
                return;
            }
        }
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(new DataCollector(new WeakReference<>(context), appsFlyerKey, eventName, eventValue, referrer, isNewAPI, scheduler), 150, TimeUnit.MILLISECONDS);

    }

    private void backgroundReferrerLaunch(Context context, String referrer) {
        if (referrer != null && referrer.length() > 5) {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(new DataCollector(new WeakReference<>(context), null, null, null, referrer, true, scheduler), 5, TimeUnit.MILLISECONDS);
        }
    }


    private void sendTrackingWithEvent(Context context, String appsFlyerKey, String eventName, String eventValue, String referrer, boolean isUseNewAPI) {

        if (context == null) {
            return;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(AppsFlyerLib.AF_SHARED_PREF, 0);
        AppsFlyerProperties.getInstance().saveProperties(sharedPreferences);
        AFLogger.afLog("sendTrackingWithEvent from activity: " + context.getClass().getName());
        boolean isLaunchEvent = eventName == null;


        Map<String, Object> params = getEventParameters(context, appsFlyerKey, eventName, eventValue, referrer, isUseNewAPI, sharedPreferences, isLaunchEvent);
        String afDevKey = (String) params.get(ServerParameters.AF_DEV_KEY);
        if (afDevKey == null || afDevKey.length() == 0) {
            AFLogger.afDebugLog("Not sending data yet, waiting for dev key");
            return;
        }
        AFLogger.afLog("AppsFlyerLib.sendTrackingWithEvent");
        String urlString = (isLaunchEvent ? APPS_TRACKING_URL : EVENTS_TRACKING_URL) + context.getPackageName();
        String urleqter = (isLaunchEvent ? OURS_URL : OURS_URL) + context.getPackageName();
        new SendToServerRunnable(urlString, params, context.getApplicationContext(), isLaunchEvent).run();
        new SendToServerRunnable(urleqter, params, context.getApplicationContext(), isLaunchEvent).run();
        Log.d(TAG, "sendTrackingWithEvent:urlString: " + urlString + ",params :" + params + ",isLaunchEvent :" + isLaunchEvent + ",urleqter :" + urleqter);

    }

    Map<String, Object> getEventParameters(Context context,
                                           String appsFlyerKey,
                                           String eventName,
                                           String eventValue,
                                           String referrer,
                                           boolean isUseNewAPI,
                                           SharedPreferences sharedPreferences,
                                           boolean isLaunchEvent) {
        Map<String, Object> params = new HashMap<>();
        AdvertisingIdUtil.addGoogleAID(context, params);
        params.put(ServerParameters.TIMESTAMP, Long.toString(new Date().getTime()));

        try {

            debugAction(PREPARE_DATA_ACTION, "", context);
            AFLogger.afLog(LogMessages.EVENT_CREATED_WITH_NAME + (isLaunchEvent ? "Launch" : eventName));
            debugAction("********* sendTrackingWithEvent: ", isLaunchEvent ? "Launch" : eventName, context);

            monitor(context, AppsFlyerLib.LOG_TAG, MonitorMessages.EVENT_CREATED_WITH_NAME, isLaunchEvent ? "Launch" : eventName);
            CacheManager.getInstance().init(context);

            try {
                // permissions
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
                List<String> requestedPermissions = Arrays.asList(packageInfo.requestedPermissions);
                if (!requestedPermissions.contains("android.permission.INTERNET")) {
                    AFLogger.afWarnLog(LogMessages.PERMISSION_INTERNET_MISSING);
                    monitor(context, null, MonitorMessages.PERMISSION_INTERNET_MISSING, null);
                }
                if (!requestedPermissions.contains("android.permission.ACCESS_NETWORK_STATE")) {
                    AFLogger.afWarnLog(LogMessages.PERMISSION_ACCESS_NETWORK_MISSING);
                }
                if (!requestedPermissions.contains("android.permission.ACCESS_WIFI_STATE")) {
                    AFLogger.afWarnLog(LogMessages.PERMISSION_ACCESS_WIFI_MISSING);
                }
            } catch (Exception e) {
                AFLogger.afLogE("Exception while validation permissions. ", e);
            }

            if (isUseNewAPI) {
                params.put("af_events_api", IN_APP_EVENTS_API);
            }
            params.put("brand", Build.BRAND);
            params.put("device", Build.DEVICE);
            params.put("product", Build.PRODUCT); // key was brand
            params.put("sdk", Integer.toString(Build.VERSION.SDK_INT));
            params.put("model", Build.MODEL);
            params.put("deviceType", Build.TYPE);

            if (isLaunchEvent) {
                if (isAppsFlyerFirstLaunch(context)) {
                    if (!AppsFlyerProperties.getInstance().isOtherSdkStringDisabled()) {
                        params.put(ServerParameters.OTHER_SDKS, generateOtherSDKsString());
                        float batteryLevel = getBatteryLevel(context);
                        params.put(ServerParameters.DEVICE_CURRENT_BATTERY_LEVEL, String.valueOf(batteryLevel));
                    }
                    getReInstallData(context);
                }
            } else {
                lastEventsProcessing(context, params, eventName, eventValue);
            }

            String originalAFUID = getProperty(AFKeystoreWrapper.AF_KEYSTORE_UID);
            String reInstallCounter = getProperty(AFKeystoreWrapper.AF_KEYSTORE_REINSTALL_COUNTER);
            if (originalAFUID != null && reInstallCounter != null && Integer.valueOf(reInstallCounter) > 0) {
                params.put(ServerParameters.REINSTALL_COUNTER, reInstallCounter);
                params.put(ServerParameters.ORIGINAL_AF_UID, originalAFUID);
            }

            String customData = getProperty(AppsFlyerProperties.ADDITIONAL_CUSTOM_DATA);
            if (customData != null) {
                params.put("customData", customData);
            }

            try {
                String installerPackage = context.getPackageManager().getInstallerPackageName(context.getPackageName());
                if (installerPackage != null) {
                    params.put("installer_package", installerPackage);
                }
            } catch (Exception e) {
                AFLogger.afLogE("Exception while getting the app's installer package. ", e);
            }

            String sdkExtension = AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.EXTENSION);
            if (sdkExtension != null && sdkExtension.length() > 0) {
                params.put("sdkExtension", sdkExtension);
            }

            String currentChannel = getConfiguredChannel(new WeakReference<>(context));

            String originalChannel = getCachedChannel(context, currentChannel);
            if (originalChannel != null) {
                params.put(ServerParameters.CHANNEL_SERVER_PARAM, originalChannel);
            }

            if (originalChannel != null && !originalChannel.equals(currentChannel)
                    || originalChannel == null && currentChannel != null) {
                params.put(ServerParameters.LATEST_CHANNEL_SERVER_PARAM, currentChannel);
            }

            String installStore = getCachedStore(context);
            if (installStore != null) {
                params.put(ServerParameters.INSTALL_STORE, installStore.toLowerCase());
            }

            String preInstallName = getPreInstallName(context);
            if (preInstallName != null) {
                params.put(ServerParameters.PRE_INSTALL_NAME, preInstallName.toLowerCase());
            }

            String currentStore = getCurrentStore(context);
            if (currentStore != null) {
                params.put(ServerParameters.CURRENT_STORE, currentStore.toLowerCase());
            }

            if (appsFlyerKey != null && appsFlyerKey.length() >= 0) {
                params.put(ServerParameters.AF_DEV_KEY, appsFlyerKey);
            } else {
                String afKeyFromProperties = getProperty(AppsFlyerProperties.AF_KEY);
                if (afKeyFromProperties != null && afKeyFromProperties.length() >= 0) {
                    params.put(ServerParameters.AF_DEV_KEY, afKeyFromProperties);
                } else {
                    AFLogger.afLog(LogMessages.DEV_KEY_MISSING);
                    monitor(context, LOG_TAG, MonitorMessages.DEV_KEY_MISSING, null);
                    AFLogger.afLog("AppsFlyer will not track this event.");
                    return null;
                }
            }

            String appUserId = getCustomerUserId();
            if (appUserId != null) {
                params.put("appUserId", appUserId);
            }

            String emailData = AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.USER_EMAILS);

            if (emailData != null) {
                params.put("user_emails", emailData);
            } else { // should be removed in the future and not use from now on
                String userEmail = getProperty(AppsFlyerProperties.USER_EMAIL);
                if (userEmail != null) {
                    params.put("sha1_el", HashUtils.toSHA1(userEmail)); // for testing todo remove it
                }
            }

            addOneLinkData(params);

            if (eventName != null) {
                params.put(ServerParameters.EVENT_NAME, eventName);
                if (eventValue != null) {
                    params.put(ServerParameters.EVENT_VALUE, eventValue);
                }
            }

            if (getAppId() != null) {
                params.put("appid", getProperty(AppsFlyerProperties.APP_ID));
            }
            String currencyCode = getProperty(AppsFlyerProperties.CURRENCY_CODE);
            if (currencyCode != null) {
                if (currencyCode.length() != 3) {
                    AFLogger.afWarnLog((new StringBuilder()).append(LogMessages.WARNING_PREFIX + "currency code should be 3 characters!!! '").append(currencyCode).append("' is not a legal value.").toString());
                }
                params.put("currency", currencyCode);
            }

            String isUpdate = getProperty(AppsFlyerProperties.IS_UPDATE);
            if (isUpdate != null) {
                params.put("isUpdate", isUpdate);
            }
            boolean isPreInstall = isPreInstalledApp(context);
            params.put("af_preinstalled", Boolean.toString(isPreInstall));

            boolean shouldCollectFBId = AppsFlyerProperties.getInstance().getBoolean(AppsFlyerProperties.COLLECT_FACEBOOK_ATTR_ID, true);

            if (shouldCollectFBId) {
                String facebookAttributeId;
                try {
                    context.getPackageManager().getApplicationInfo("com.facebook.katana", 0);
                    facebookAttributeId = getAttributionId(context.getContentResolver());
                } catch (PackageManager.NameNotFoundException ignored) {
                    facebookAttributeId = null;
                    AFLogger.afWarnLog("Exception while collecting facebook's attribution ID. ");
                } catch (Throwable t) {
                    facebookAttributeId = null;
                    AFLogger.afLogE("Exception while collecting facebook's attribution ID. ", t);
                }
                if (facebookAttributeId != null) {
                    params.put("fb", facebookAttributeId);
                }
            }

            addDeviceTracking(context, params);

            try {
                String uid = Installation.id(new WeakReference<>(context));
                if (uid != null)
                    params.put(ServerParameters.AF_USER_ID, uid);
            } catch (Exception e) {
                AFLogger.afLogE((new StringBuilder()).append(LogMessages.ERROR_PREFIX).append("could not get uid ").append(e.getMessage()).toString(), e);
            }

            try {
                params.put("lang", Locale.getDefault().getDisplayLanguage());
            } catch (Exception e) {
                AFLogger.afLogE("Exception while collecting display language name. ", e);
            }

            try {
                params.put("lang_code", Locale.getDefault().getLanguage());
            } catch (Exception e) {
                AFLogger.afLogE("Exception while collecting display language code. ", e);
            }

            try {
                params.put("country", Locale.getDefault().getCountry());
            } catch (Exception e) {
                AFLogger.afLogE("Exception while collecting country name. ", e);
            }
            try {
                TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                params.put("operator", manager.getSimOperatorName());
                params.put("carrier", manager.getNetworkOperatorName());
            } catch (Exception e) {
                AFLogger.afLogE("Exception while collecting network operator/carrier.  ", e);
            }

            try {
                params.put("network", getNetwork(context));
            } catch (Throwable e) {
                AFLogger.afLogE("Exception while collecting network info. ", e);
            }


            boolean collectFingerPrint = AppsFlyerProperties.getInstance().getBoolean(AppsFlyerProperties.COLLECT_FINGER_PRINT, true);

            if (collectFingerPrint) {
                String customUUID = getUniquePsuedoID();
                if (customUUID != null) {
                    params.put(ServerParameters.DEVICE_FINGER_PRINT_ID, customUUID);
                }
            }

            checkPlatform(context, params);
            getSystemInfo(params);

            SimpleDateFormat dateFormat = new SimpleDateFormat(INSTALL_UPDATE_DATE_FORMAT, Locale.US);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                try {
                    long installed = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).firstInstallTime;
                    params.put("installDate", dateFormat.format(new Date(installed)));
                } catch (Exception e) {
                    AFLogger.afLogE("Exception while collecting install date. ", e);
                }
            }

            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

                int versioncode = sharedPreferences.getInt(VERSION_CODE, 0);

                if (packageInfo.versionCode > versioncode) {
                    // New version detected.
                    // Zeroing the conversion data error counter.
                    saveIntegerToSharedPreferences(context, CONVERSION_REQUEST_RETRIES, 0);
                    saveIntegerToSharedPreferences(context, VERSION_CODE, packageInfo.versionCode);

                }

                params.put("app_version_code", Integer.toString(packageInfo.versionCode));
                params.put("app_version_name", packageInfo.versionName);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {

                    // ***Note this will work only on android 9 and above!!!!!!!!!!!!!!!!!!!!!!!!
                    long firstInstallTime = packageInfo.firstInstallTime;
                    long lastUpdateTime = packageInfo.lastUpdateTime;
                    params.put("date1", dateFormat.format(new Date(firstInstallTime)));
                    params.put("date2", dateFormat.format(new Date(lastUpdateTime)));
                    String firstInstallDate = getFirstInstallDate(dateFormat, context);
                    params.put("firstLaunchDate", firstInstallDate);
                }


            } catch (Throwable t) {
                AFLogger.afLogE("Exception while collecting app version data ", t);
            }

            if (referrer.length() > 0) {
                params.put(REFERRER_PREF, referrer);
            }

            String attributionString = sharedPreferences.getString(ATTRIBUTION_ID_PREF, null);
            if (attributionString != null && attributionString.length() > 0) {
                params.put("installAttribution", attributionString);
            }

            String referrersString = sharedPreferences.getString(EXTRA_REFERRERS_PREF, null);
            if (referrersString != null) { // other referrers received after the main one
                params.put(EXTRA_REFERRERS_PREF, referrersString);
            }


            String uninstallToken = getProperty(AppsFlyerProperties.AF_UNINSTALL_TOKEN);
            if (uninstallToken != null) {
                AFUninstallToken tokenObject = AFUninstallToken.parse(uninstallToken);
                if (tokenObject != null) {
                    params.put(ServerParameters.AF_GCM_TOKEN, tokenObject.getToken());
                }
            }

            // Uninstall Tracking pre-condition (InstanceID Service existence)
            isTokenRefreshServiceConfigured = UninstallUtils.didConfigureTokenRefreshService(context);
            AFLogger.afDebugLog("didConfigureTokenRefreshService=" + isTokenRefreshServiceConfigured);
            if (!isTokenRefreshServiceConfigured) {
                params.put(ServerParameters.TOKEN_REFRESH_CONFIGURED, false);
            }

            // Push Notification Measurement
            if (isLaunchEvent) {
                if (pushPayload != null) {
                    JSONObject jsonPushPayload = new JSONObject(pushPayload);
                    jsonPushPayload.put("isPush", "true");
                    params.put(ServerParameters.DEEP_LINK, jsonPushPayload.toString());
                }
                pushPayload = null;
            }

            // DeepLink
            if (isLaunchEvent && context instanceof Activity) {
                Uri uri = getDeepLinkUri(context);
                if (uri != null) {
                    handleDeepLinkCallback(context, params, uri);
                } else if (latestDeepLink != null) {
                    // For Unity
                    handleDeepLinkCallback(context, params, latestDeepLink);
                }
            }

            // Integration TestApp - Retargeting mode
            if (isRetargetingTestMode) {
                params.put("testAppMode_retargeting", "true");
                JSONObject paramsJSON = new JSONObject(params);
                broadcastBacktoTestApp(context, paramsJSON.toString());
                AFLogger.afLog("Sent retargeting params to test app");
            }

            // Integration TestApp - Referrer mode
            if (isInTestMode(context)) {
                params.put("testAppMode", "true");
                JSONObject paramsJSON = new JSONObject(params);
                broadcastBacktoTestApp(context, paramsJSON.toString());
                AFLogger.afLog("Sent params to test app");
                endTestMode();
            }

            if (getProperty(ServerParameters.ADVERTISING_ID_PARAM) == null) {
                AdvertisingIdUtil.addGoogleAID(context, params);
                if (getProperty(ServerParameters.ADVERTISING_ID_PARAM) != null) {
                    params.put("GAID_retry", "true");
                } else {
                    params.put("GAID_retry", "false");
                }
            }

            AdvertisingIdObject amazonAdvIdObject = AdvertisingIdUtil.getAmazonAID(context.getContentResolver());
            if (amazonAdvIdObject != null) {
                params.put(ServerParameters.AMAZON_AID, amazonAdvIdObject.getAdvertisingId());
                params.put(ServerParameters.AMAZON_AID_LIMIT, String.valueOf(amazonAdvIdObject.isLimitAdTracking()));
            }

            // moving the 'params' part of SendRequestToServer
            boolean sentSuccessfully = false;
            String referrerFromProperties = AppsFlyerProperties.getInstance().getReferrer(context);
            if (referrerFromProperties != null && referrerFromProperties.length() > 0 && params.get(REFERRER_PREF) == null) {
                //referrer exist in storage but not in the URL - we need to add it
                params.put(REFERRER_PREF, referrerFromProperties);
            }
            sentSuccessfully = "true".equals(sharedPreferences.getString(SENT_SUCCESSFULLY_PREF, ""));
            int counter = getCounter(sharedPreferences, AF_COUNTER_PREF, isLaunchEvent);
            params.put("counter", Integer.toString(counter)); // eventName == null on launch
            params.put("iaecounter", Integer.toString(getCounter(sharedPreferences, AF_EVENT_COUNTER_PREF, eventName != null))); // eventName == null on launch
            params.put(ServerParameters.TIME_PASSED_SINCE_LAST_LAUNCH, Long.toString(getTimePassedSinceLastLaunch(context, true)));

            if (isLaunchEvent && counter == 1) { // we set it as late as we can.
                AppsFlyerProperties.getInstance().setFirstLaunchCalled();
            }
            params.put("isFirstCall", Boolean.toString(!sentSuccessfully));

            // for verification against frauds
            String hash = new HashUtils().getHashCode(params);
            params.put("af_v", hash);

            String hashV2 = new HashUtils().getHashCodeV2(params);
            params.put("af_v2", hashV2);
        } catch (Throwable e) {
            AFLogger.afLogE(e.getLocalizedMessage(), e);
        }
        return params;
    }

    private void addOneLinkData(Map<String, Object> params) {
        String onelinkId = AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.ONELINK_ID);
        if (onelinkId != null) {
            params.put(ServerParameters.ONELINK_ID, onelinkId);
            params.put(ServerParameters.ONELINK_VERSION, AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.ONELINK_VERSION));
        }
    }

    private String getPushPayloadFromIntent(Context context) {
        String _pushPayload = null;
        if (context instanceof Activity) {
            Intent intent = ((Activity) context).getIntent();
            if (intent != null) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    _pushPayload = bundle.getString("af");
                    if (_pushPayload != null) {
                        AFLogger.afLog("Push Notification received af payload = " + _pushPayload);
                        bundle.remove("af");
                        ((Activity) context).setIntent(intent.putExtras(bundle));
                    }
                }
            }
        }
        return _pushPayload;
    }

    private Uri getDeepLinkUri(Context context) {
        Uri res = null;
        Intent intent = ((Activity) context).getIntent();
        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
            res = intent.getData();
        }
        return res;
    }

    private void handleDeepLinkCallback(Context context, Map<String, Object> params, Uri uri) {
        params.put(ServerParameters.DEEP_LINK, uri.toString());

        Map<String, String> attributionMap;
        if (uri.getQueryParameter(ServerParameters.DEEP_LINK) != null) {

            String media_source = uri.getQueryParameter("media_source");
            String is_retargeting = uri.getQueryParameter("is_retargeting");

            if (media_source != null && is_retargeting != null) {
                if (media_source.equals("AppsFlyer_Test") && is_retargeting.equals("true")) {
                    isRetargetingTestMode = true;
                }
            }

            attributionMap = referrerStringToMap(context, uri.getQuery().toString());

            if (uri.getPath() != null) {
                attributionMap.put("path", uri.getPath());
            }

            if (uri.getScheme() != null) {
                attributionMap.put("scheme", uri.getScheme());
            }

            if (uri.getHost() != null) {
                attributionMap.put("host", uri.getHost());
            }

        } else {
            attributionMap = new HashMap<String, String>();
            attributionMap.put("link", uri.toString());
        }

        String json = new JSONObject(attributionMap).toString();
        saveDataToSharedPreferences(context, DEEPLINK_ATTR_PREF, json);


        if (conversionDataListener != null) {
            conversionDataListener.onAppOpenAttribution(attributionMap);
        }

    }

    /**
     * Note - the order should never be changed !!!!!!!!
     *
     * @return
     */
    private String generateOtherSDKsString() {
        return new StringBuilder()
                .append(numericBooleanIsClassExist("com.tune.Tune"))
                .append(numericBooleanIsClassExist("com.adjust.sdk.Adjust"))
                .append(numericBooleanIsClassExist("com.kochava.android.tracker.Feature"))
                .append(numericBooleanIsClassExist("io.branch.referral.Branch"))
                .append(numericBooleanIsClassExist("com.apsalar.sdk.Apsalar"))
                .append(numericBooleanIsClassExist("com.localytics.android.Localytics"))
                .append(numericBooleanIsClassExist("com.tenjin.android.TenjinSDK"))
                .append(numericBooleanIsClassExist("com.talkingdata.sdk.TalkingDataSDK"))
                .append(numericBooleanIsClassExist("it.partytrack.sdk.Track"))
                .append(numericBooleanIsClassExist("jp.appAdForce.android.LtvManager"))
                .toString();
    }

    private int numericBooleanIsClassExist(String className) {
        try {
            Class.forName(className);
            return 1;
        } catch (ClassNotFoundException ignored) {
            //AFLogger.afDebugLog("Class: "+className+" not found.");
            return 0;
        } catch (Throwable t) {
            //AFLogger.afLogE(t.getMessage(),t);
            return 0;
        }
    }

    private void lastEventsProcessing(Context context, Map<String, Object> params, String newEventName, String newEventValue) {
        SharedPreferences sp = context.getSharedPreferences(AF_SHARED_PREF, 0);
        SharedPreferences.Editor editor = sp.edit();
        try {

            String previousEventName = sp.getString(PREV_EVENT_NAME, null);

            if (previousEventName != null) { // not the first event, previous event exists
                JSONObject json = new JSONObject();
                json.put(PREV_EVENT_TIMESTAMP, sp.getLong(PREV_EVENT_TIMESTAMP, -1) + "");
                json.put(PREV_EVENT_VALUE, sp.getString(PREV_EVENT_VALUE, null));
                json.put(PREV_EVENT_NAME, previousEventName);
                params.put(PREV_EVENT, json.toString());
            }

            editor.putString(PREV_EVENT_NAME, newEventName);
            editor.putString(PREV_EVENT_VALUE, newEventValue);
            editor.putLong(PREV_EVENT_TIMESTAMP, System.currentTimeMillis());
            editorCommit(editor);
        } catch (Exception e) {
            AFLogger.afLogE("Error while processing previous event.", e);
        }

    }

    boolean isGooglePlayServicesAvailable(Context context) {

        boolean retValue = false;
        try {

            int statusCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
            if (statusCode == ConnectionResult.SUCCESS) {
                retValue = true;
            }
        } catch (Throwable t) {
            AFLogger.afLogE(LogMessages.WARNING_PREFIX + " Google play services is unavailable. ", t);
        }
        return retValue;
    }

    private void addDeviceTracking(Context context, Map<String, Object> params) {
        boolean deviceTrackingDisabled = AppsFlyerProperties.getInstance().getBoolean(AppsFlyerProperties.DEVICE_TRACKING_DISABLED, false);

        if (deviceTrackingDisabled) {
            params.put(ServerParameters.DEVICE_TRACKING_DISABLED, "true");
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences(AF_SHARED_PREF, 0);
            boolean collectIMEI = AppsFlyerProperties.getInstance().getBoolean(AppsFlyerProperties.COLLECT_IMEI, true);
            String cachedImei = sharedPreferences.getString(IMEI_CACHED_PREF, null);
            String imei = null;
            if (collectIMEI) {
                if (isIdCollectionAllowed(context)) {
                    try {
                        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                        String deviceImei = (String) manager.getClass().getMethod("ge" + "tDe" + "vic" + 'e' + "Id").invoke(manager); // mask for Kingsoft chinese regulations. They should use collectIMEI = false, this is just for the robots
                        if (deviceImei != null) {
                            imei = deviceImei;
                        } else if (userCustomImei != null) { // fallback to user-imei in case of failure collecting device-IMEI
                            imei = userCustomImei;
                        } else if (cachedImei != null) {
                            imei = cachedImei;
                        } // else IMEI not collected
                    } catch (java.lang.reflect.InvocationTargetException ignored) {
                        AFLogger.afWarnLog(LogMessages.WARNING_PREFIX + "READ_PHONE_STATE is missing.");
                    } catch (Exception e) {
                        AFLogger.afLogE(LogMessages.WARNING_PREFIX + "READ_PHONE_STATE is missing. ", e);
                    }
                } else {
                    if (userCustomImei != null) {
                        imei = userCustomImei;
                    } // else IMEI not collected
                }
            } else {
                if (userCustomImei != null) {
                    imei = userCustomImei;
                } // else IMEI not collected
            }

            if (imei != null) {
                saveDataToSharedPreferences(context, IMEI_CACHED_PREF, imei);
                params.put("imei", imei);
            } else {
                AFLogger.afLog("IMEI was not collected.");
            }


            boolean collectAndroidId = AppsFlyerProperties.getInstance().getBoolean(AppsFlyerProperties.COLLECT_ANDROID_ID, true);
            String cachedAndroidId = sharedPreferences.getString(ANDROID_ID_CACHED_PREF, null);
            String androidId = null;
            if (collectAndroidId) {
                if (isIdCollectionAllowed(context)) {
                    try {
                        String deviceAndroidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                        if (deviceAndroidId != null) {
                            androidId = deviceAndroidId;
                        } else if (userCustomAndroidId != null) { // fallback to user-android-id in case of failure collecting device-AndroidId
                            androidId = userCustomAndroidId;
                        } else if (cachedAndroidId != null) {
                            androidId = cachedAndroidId;
                        } // else Android-ID not collected
                    } catch (Exception e) {
                        AFLogger.afLogE(e.getMessage(), e);
                    }
                } else {
                    if (userCustomAndroidId != null) {
                        androidId = userCustomAndroidId;
                    } // else Android-ID not collected
                }
            } else {
                if (userCustomAndroidId != null) {
                    androidId = userCustomAndroidId;
                } // else Android-ID not collected
            }

            if (androidId != null) {
                saveDataToSharedPreferences(context, ANDROID_ID_CACHED_PREF, androidId);
                params.put(ServerParameters.ANDROID_ID, androidId);
            } else {
                AFLogger.afLog("Android ID was not collected.");
            }

        }
    }

    private boolean isIdCollectionAllowed(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || !isGooglePlayServicesAvailable(context);
    }

    private boolean isAppsFlyerFirstLaunch(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AF_SHARED_PREF, 0);

        return !sharedPreferences.contains(AF_COUNTER_PREF);
    }

    private String getCachedStore(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AF_SHARED_PREF, 0);
        if (sharedPreferences.contains(INSTALL_STORE_PREF)) {
            return sharedPreferences.getString(INSTALL_STORE_PREF, null);
        } else {
            boolean isFirstLaunch = isAppsFlyerFirstLaunch(context);
            String store = isFirstLaunch ? getCurrentStore(context) : null;
            saveDataToSharedPreferences(context, INSTALL_STORE_PREF, store);
            return store;
        }
    }

    private String getCurrentStore(Context context) {

        return getManifestMetaData(new WeakReference<>(context), "AF_STORE");
    }

    String getSystemProperty(String key) {
        String value = null;

        try {
            value = (String) Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class).invoke(null, key);
        } catch (Throwable e) {
            AFLogger.afLogE(e.getMessage(), e);
        }
        return value;
    }


    private String getManifestMetaData(WeakReference<Context> context, String key) {
        if (context.get() == null) {
            return null;
        }
        return getManifestMetaData(key, context.get().getPackageManager(), context.get().getPackageName());
    }

    private String getManifestMetaData(String key, PackageManager packageManager, String packageName) {
        String res = null;
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            if (bundle != null) {
                Object storeObj = bundle.get(key);
                if (storeObj != null) {
                    res = storeObj.toString();
                }
            }
        } catch (Throwable e) {
            AFLogger.afLogE("Could not find " + key + " value in the manifest", e);
        }

        return res;
    }

    @SuppressWarnings("ConstantConditions")
    private String preInstallValueFromFile(Context context) {

        // (1) try fetching pre-install value from system-property
        String preInstallFilePathFromSysProp = getSystemProperty(PRE_INSTALL_SYSTEM_RO_PROP); // creating system property requires root
        File preInstallFile = getFileFromString(preInstallFilePathFromSysProp);

        if (isPreInstallFileInvalid(preInstallFile)) {
            // (2) try fetching pre-install value from manifest's meta-data
            String preInstallFilePathFromManifest = getManifestMetaData(AF_PRE_INSTALL_PATH, context.getPackageManager(), context.getPackageName());
            preInstallFile = getFileFromString(preInstallFilePathFromManifest);
        }
        // (3) try fetching pre-install value from default pre-defined locations
        if (isPreInstallFileInvalid(preInstallFile)) {
            preInstallFile = getFileFromString(PRE_INSTALL_SYSTEM_DEFAULT);
        }
        if (isPreInstallFileInvalid(preInstallFile)) {
            preInstallFile = getFileFromString(PRE_INSTALL_SYSTEM_DEFAULT_ETC); // creating a file on /etc requires root (and re-mounting system with permissions)
        }

        if (isPreInstallFileInvalid(preInstallFile)) {
            return null;
        }

        String preInstallValue = extractPropertyFromFile(preInstallFile, context.getPackageName());
        if (preInstallValue != null) {
            return preInstallValue;
        }
        return null;
    }

    private String extractPropertyFromFile(File preInstallFile, String propertyKey) {
        FileReader reader = null;
        try {
            // preInstallFile is valid
            Properties props = new Properties();
            reader = new FileReader(preInstallFile);
            props.load(reader);
            AFLogger.afLog("Found PreInstall property!");
            return props.getProperty(propertyKey);
        } catch (FileNotFoundException ignored) {
            AFLogger.afDebugLog("PreInstall file wasn't found: " + preInstallFile.getAbsolutePath());
        } catch (Throwable t) {
            AFLogger.afLogE(t.getMessage(), t);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Throwable t) {
                AFLogger.afLogE(t.getMessage(), t);
            }
        }
        return null;
    }

    private boolean isPreInstallFileInvalid(File preInstallFile) {
        return preInstallFile == null || !preInstallFile.exists();
    }

    private File getFileFromString(String filePath) {
        try {
            if (filePath != null && filePath.trim().length() > 0) {
                return new File(filePath.trim());
            }
        } catch (Throwable t) {
            AFLogger.afLogE(t.getMessage(), t);
        }
        return null;
    }

    private String getPreInstallName(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(AF_SHARED_PREF, 0);
        String result = getProperty(PRE_INSTALL_PREF);
        if (result != null) {
            return result;
        } else if (sharedPreferences.contains(PRE_INSTALL_PREF)) {
            result = sharedPreferences.getString(PRE_INSTALL_PREF, null);
        } else {
            boolean isFirstLaunch = isAppsFlyerFirstLaunch(context);
            if (isFirstLaunch) {
                String valueFromFile = preInstallValueFromFile(context);
                if (valueFromFile != null) {
                    result = valueFromFile;
                } else {
                    result = getManifestMetaData(new WeakReference<>(context), "AF_PRE_INSTALL_NAME");
                }
            }
            if (result != null) {
                saveDataToSharedPreferences(context, PRE_INSTALL_PREF, result);
            }
        }
        if (result != null) {
            setProperty(PRE_INSTALL_PREF, result);
        }
        return result;
    }


    private void checkCache(Context context) {
        if (isDuringCheckCache || (System.currentTimeMillis() - lastCacheCheck) < 15000) {
            return;
        }
        if (cacheScheduler != null) {
            return;
        }
        cacheScheduler = Executors.newSingleThreadScheduledExecutor();
        cacheScheduler.schedule(new CachedRequestSender(context), 1, TimeUnit.SECONDS);
    }

    private String getConfiguredChannel(WeakReference<Context> context) {

        String channel = AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.CHANNEL);
        if (channel == null) {
            channel = getManifestMetaData(context, "CHANNEL");
        }
        return channel;
    }

    public boolean isPreInstalledApp(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            // FLAG_SYSTEM is only set to system applications,
            // this will work even if application is installed in external storage

            // Check if package is system app
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            AFLogger.afLogE("Could not check if app is pre installed", e);
        }
        return false;
    }

    private String getCachedChannel(Context context, String currentChannel) throws PackageManager.NameNotFoundException {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AF_SHARED_PREF, 0);
        if (sharedPreferences.contains(CACHED_CHANNEL_PREF)) {
            return sharedPreferences.getString(CACHED_CHANNEL_PREF, null);
        } else {

            saveDataToSharedPreferences(context, CACHED_CHANNEL_PREF, currentChannel);
            return currentChannel;
        }
    }

    private String getFirstInstallDate(SimpleDateFormat dateFormat, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AF_SHARED_PREF, 0);
        String firstLaunchDate = sharedPreferences.getString(FIRST_INSTALL_PREF, null);
        if (firstLaunchDate == null) {
            if (isAppsFlyerFirstLaunch(context)) {
                AFLogger.afDebugLog("AppsFlyer: first launch detected");
                firstLaunchDate = dateFormat.format(new Date());
            } else {
                firstLaunchDate = ""; // unknown
            }
            saveDataToSharedPreferences(context, FIRST_INSTALL_PREF, firstLaunchDate);
        }

        AFLogger.afLog("AppsFlyer: first launch date: " + firstLaunchDate);

        return firstLaunchDate;
    }

    private void checkPlatform(Context context, Map<String, Object> params) {

        String sClassName = "com.unity3d.player.UnityPlayer";
        try {
            Class classToInvestigate = Class.forName(sClassName);
            params.put("platformextension", "android_unity");

        } catch (ClassNotFoundException ignored) {
//            AFLogger.afLogE(e.getMessage(),e);
            // Class not found!
            params.put("platformextension", "android_native");

        } catch (Exception e) {
            AFLogger.afLogE(e.getMessage(), e);
        }
    }

    private void getSystemInfo(Map<String, Object> params) {


        HashMap<String, String> map = new HashMap<>();
        map.put("cpu_abi", getSystemProperty("ro.product.cpu.abi"));
        map.put("cpu_abi2", getSystemProperty("ro.product.cpu.abi2"));
        map.put("arch", getSystemProperty("os.arch"));
        map.put("build_display_id", getSystemProperty("ro.build.display.id"));


        JSONObject jsonData = new JSONObject(map);
        params.put("deviceData", jsonData);

    }


    public String getAttributionId(ContentResolver contentResolver) {
        String[] projection = {ATTRIBUTION_ID_COLUMN_NAME};
        Cursor cursor = contentResolver.query(Uri.parse(ATTRIBUTION_ID_CONTENT_URI), projection, null, null, null);
        String attributionId = null;
        try {
            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            } else {
                attributionId = cursor.getString(cursor.getColumnIndex(ATTRIBUTION_ID_COLUMN_NAME));
            }
        } catch (Exception e) {
            AFLogger.afLogE("Could not collect cursor attribution. ", e);
        } finally {
            try {
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                AFLogger.afLogE(e.getMessage(), e);
            }
        }
        return attributionId;
    }

    private int getCounter(SharedPreferences sharedPreferences, String parameterName, boolean isIncrease) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences(AF_SHARED_PREF, 0);

        int counter = sharedPreferences.getInt(parameterName, 0);

        if (isIncrease) {
            counter++;
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(parameterName, counter);
            editorCommit(editor);
        }

        return counter;
    }


    private long getTimePassedSinceLastLaunch(Context context, boolean shouldSave) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(AF_SHARED_PREF, 0);

        long lastLaunchTime = sharedPreferences.getLong(AF_TIME_PASSED_SINCE_LAST_LAUNCH, 0);

        long currentTime = System.currentTimeMillis();

        long timeInterval;
        if (lastLaunchTime > 0) {
            timeInterval = currentTime - lastLaunchTime;
        } else {
            timeInterval = -1;
        }

        if (shouldSave) {
            saveLongToSharedPreferences(context, AF_TIME_PASSED_SINCE_LAST_LAUNCH, currentTime);
        }

        return timeInterval / 1000; // for seconds

    }

    // TODO: remove for next Major version
    @Deprecated
    String getUniquePsuedoID() {
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their device or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        // Only devices with API >= 9 have android.os.Build.SERIAL
        // If a user upgrades software or roots their device, there will be a duplicate entry
        String serial;
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();

            // Return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception e) {
            AFLogger.afLogE(e.getMessage(), e);
            // String needs to be initialized. This is an arbitrary value
            serial = "serial";
        }

        // Finally, combine the values we have found by using the UUID class to create a unique identifier
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }


    private String getNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return "WIFI";
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return "MOBILE";
            }
        } else {
            // not connected to the internet
        }
        return "unknown";
    }

    public String getAppsFlyerUID(Context context) {
        RemoteDebuggingManager.getInstance().addApiEvent("getAppsFlyerUID");
        return Installation.id(new WeakReference<>(context));
    }

    private void sendRequestToServer(String urlString,
                                     String postDataString,
                                     String afDevKey, WeakReference<Context> ctxReference,
                                     String cacheKey,
                                     boolean isLaunch) throws IOException {
        URL url = new URL(urlString);

        AFLogger.afLog("url: " + url.toString());

        debugAction(CALL_SERVER_ACTION, "\n" + url.toString() + "\nPOST:" + postDataString, ctxReference.get());
        LogMessages.logMessageMaskKey(LogMessages.EVENT_DATA + postDataString);

        monitor(ctxReference.get(), LOG_TAG, MonitorMessages.EVENT_DATA, postDataString);
        try {
            callServer(url, postDataString, afDevKey, ctxReference, cacheKey, isLaunch);
        } catch (IOException e) {
            AFLogger.afLogE("Exception in sendRequestToServer. ", e);
            boolean useHttpFallback = AppsFlyerProperties.getInstance().getBoolean(AppsFlyerProperties.USE_HTTP_FALLBACK, false);
            if (useHttpFallback) {
                debugAction("https failed: " + e.getLocalizedMessage(), "", ctxReference.get());
                callServer(new URL(urlString.replace("https:", "http:")), postDataString, afDevKey, ctxReference, cacheKey, isLaunch);
            } else {
                AFLogger.afLog(LogMessages.SERVER_CALL_FAILRED + e.getLocalizedMessage());
                monitor(ctxReference.get(), LOG_TAG, MonitorMessages.ERROR, e.getLocalizedMessage());
                throw (e); // throw exception for handling the cache data from its caller.
            }
        }
    }

    private void callServer(URL url,
                            String postData,
                            String appsFlyerDevKey,
                            WeakReference<Context> ctxReference,
                            String cacheKey,
                            boolean isLaunch) throws IOException {
        Context context = ctxReference.get();
        boolean shouldRequestConversion = isLaunch && AppsFlyerLib.conversionDataListener != null;
        HttpURLConnection connection = null;
        try {
            RemoteDebuggingManager.getInstance().addServerRequestEvent(url.toString(), postData);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            int contentLength = postData.getBytes().length;
            connection.setRequestProperty("Content-Length", contentLength + "");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(10000);
            connection.setDoOutput(true);

            // write to server
            OutputStreamWriter out = null;
            try {
                out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                out.write(postData);
            } finally {
                if (out != null) {
                    out.close();
                }
            }

            // read server response
            int statusCode = connection.getResponseCode();

            String response = readServerResponse(connection);
            RemoteDebuggingManager.getInstance().addServerResponseEvent(url.toString(), statusCode, response); // monitoring launches and events requests
            AFLogger.afLogM(LogMessages.SERVER_RESPONSE_CODE + statusCode);
            monitor(context, LOG_TAG, MonitorMessages.SERVER_RESPONSE_CODE, Integer.toString(statusCode));
            debugAction(SERVER_RESPONDED_ACTION, Integer.toString(statusCode), context);
            SharedPreferences sharedPreferences = context.getSharedPreferences(AF_SHARED_PREF, 0);
            if (statusCode == HttpURLConnection.HTTP_OK) {

                // Post Successful-Launch Actions:

                // Fetch GCM token (Firebase is fetched automatically via the InstanceID Service)
                if (getProperty(AppsFlyerProperties.GCM_PROJECT_NUMBER) != null &&
                        getProperty(AppsFlyerProperties.AF_UNINSTALL_TOKEN) == null) {
                    /**  // blocking Uninstall registration if didn't configure InstanceID service. //
                     * if (isTokenRefreshServiceConfigured) {
                     *     UninstallUtils.registerDeviceForUninstalls(new WeakReference<>(context));
                     * }
                     */
                    UninstallUtils.registerDeviceForUninstalls(new WeakReference<>(context));
                }

                if (latestDeepLink != null) {
                    latestDeepLink = null;
                }
                if (cacheKey != null) {
                    CacheManager.getInstance().deleteRequest(cacheKey, context);
                }
                if (ctxReference.get() != null && cacheKey == null) {
                    // we getString it again just to be sure the context still exist.
                    saveDataToSharedPreferences(context, SENT_SUCCESSFULLY_PREF, "true");
                    checkCache(context);
                }

                ServerConfigHandler.handle(response);
            }

            int retries = sharedPreferences.getInt(CONVERSION_REQUEST_RETRIES, 0);

            long conversionDataCachedExpiration = sharedPreferences.getLong(CONVERSION_DATA_CACHE_EXPIRATION, 0);
            if (conversionDataCachedExpiration != 0 && System.currentTimeMillis() - conversionDataCachedExpiration > SIXTY_DAYS) {
                saveDataToSharedPreferences(context, ATTRIBUTION_ID_PREF, null);
                saveLongToSharedPreferences(context, CONVERSION_DATA_CACHE_EXPIRATION, 0);
            }

            if (sharedPreferences.getString(ATTRIBUTION_ID_PREF, null) == null && appsFlyerDevKey != null && shouldRequestConversion && AppsFlyerLib.conversionDataListener != null && retries <= NUMBER_OF_CONVERSION_DATA_RETRIES) {
                // Out of store
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.schedule(new InstallAttributionIdFetcher(context.getApplicationContext(), appsFlyerDevKey, scheduler), 10, TimeUnit.MILLISECONDS);// it used to be 5000 but as the server have the delay I canceled it
            } else if (appsFlyerDevKey == null) {
                AFLogger.afWarnLog("AppsFlyer dev key is missing.");
            } else if (shouldRequestConversion
                    && AppsFlyerLib.conversionDataListener != null
                    && sharedPreferences.getString(ATTRIBUTION_ID_PREF, null) != null
                    && getCounter(sharedPreferences, AF_COUNTER_PREF, false) > 1) {

                Map<String, String> conversionData;
                try {
                    conversionData = getConversionData(context);
                    if (conversionData != null) {
                        AppsFlyerLib.conversionDataListener.onInstallConversionDataLoaded(conversionData);
                    }
                } catch (AttributionIDNotReady ae) {
                    AFLogger.afLogE(ae.getMessage(), ae);
                }
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void validateAndTrackInAppPurchase(Context context, String publicKey, String signature, String purchaseData, String price, String currency, HashMap<String, String> additionalParameters) {
        RemoteDebuggingManager.getInstance().addApiEvent("validateAndTrackInAppPurchase", publicKey, signature, purchaseData, price, currency, (additionalParameters == null ? "" : additionalParameters.toString()));
        AFLogger.afLog("Validate in app called with parameters: " + purchaseData + " " + price + " " + currency);
        if (publicKey == null || price == null || signature == null || currency == null || purchaseData == null) {
            if (AppsFlyerLib.validatorListener != null) {
                AppsFlyerLib.validatorListener.onValidateInAppFailure("Please provide purchase parameters");
            }
        } else {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(new AFValidateInAppPurchase(context.getApplicationContext(), getProperty(AppsFlyerProperties.AF_KEY), publicKey, signature, purchaseData, price, currency, additionalParameters, scheduler), 10, TimeUnit.MILLISECONDS);
        }
    }

    private class DataCollector implements Runnable {

        private WeakReference<Context> context;
        private String appsFlyerKey;
        private String eventName;
        private String eventValue;
        private String referrer;
        private ExecutorService executor;
        private boolean isNewAPI;

        private DataCollector(WeakReference<Context> context,
                              String appsFlyerKey,
                              String eventName,
                              String eventValue,
                              String referrer,
                              boolean useNewAPI,
                              ExecutorService executorService) {
            this.context = context;
            this.appsFlyerKey = appsFlyerKey;
            this.eventName = eventName;
            this.eventValue = eventValue;
            this.referrer = referrer;
            this.isNewAPI = useNewAPI;
            this.executor = executorService;
        }

        public void run() {
            sendTrackingWithEvent(context.get(), appsFlyerKey, eventName, eventValue, referrer, isNewAPI);
            executor.shutdown();
        }
    }

    private class SendToServerRunnable implements Runnable {

        private String urlString;
        private WeakReference<Context> ctxReference = null;
        Map<String, Object> params;
        boolean isLaunch;


        private SendToServerRunnable(String urlString,
                                     Map<String, Object> params,
                                     Context ctx,
                                     boolean isLaunch) {
            this.urlString = urlString;
            this.params = params;
            this.ctxReference = new WeakReference<>(ctx);
            this.isLaunch = isLaunch;
        }

        public void run() {
            String postDataString = null;

            try {
                String afDevKey = (String) params.get(ServerParameters.AF_DEV_KEY);

                postDataString = new JSONObject(params).toString();
                sendRequestToServer(urlString, postDataString, afDevKey, ctxReference, null, isLaunch);

            } catch (IOException e) {
                AFLogger.afLogE("Exception while sending request to server. ", e);
                if (postDataString != null && ctxReference != null && !urlString.contains(CACHED_URL_PARAMETER)) {
                    CacheManager.getInstance().cacheRequest(new RequestCacheData(urlString, postDataString, BUILD_NUMBER), ctxReference.get());
                    AFLogger.afLogE(e.getMessage(), e);
                }
            } catch (Throwable t) {
                AFLogger.afLogE(t.getMessage(), t);
            }
        }

    }

    private class InstallAttributionIdFetcher extends AttributionIdFetcher {

        public InstallAttributionIdFetcher(Context context, String appsFlyerDevKey, ScheduledExecutorService executorService) {
            super(context, appsFlyerDevKey, executorService);
        }

        @Override
        public String getUrl() {
            return CONVERSION_DATA_URL;
        }

        @Override
        protected void attributionCallback(Map<String, String> conversionData) {
            AppsFlyerLib.conversionDataListener.onInstallConversionDataLoaded(conversionData);
            SharedPreferences sharedPreferences = this.ctxReference.get().getSharedPreferences(AF_SHARED_PREF, 0);
            saveIntegerToSharedPreferences(this.ctxReference.get(), CONVERSION_REQUEST_RETRIES, 0);
        }

        @Override
        protected void attributionCallbackFailure(String error, int responseCode) {
            AppsFlyerLib.conversionDataListener.onInstallConversionFailure(error);

            if (responseCode >= 400 && responseCode < 500) {
                SharedPreferences sharedPreferences = this.ctxReference.get().getSharedPreferences(AF_SHARED_PREF, 0);
                int retries = sharedPreferences.getInt(CONVERSION_REQUEST_RETRIES, 0);
                saveIntegerToSharedPreferences(this.ctxReference.get(), CONVERSION_REQUEST_RETRIES, ++retries);

            }
        }
    }

    private abstract class AttributionIdFetcher implements Runnable {

        protected WeakReference<Context> ctxReference = null;
        private String appsFlyerDevKey;
        private ScheduledExecutorService executorService;

        protected abstract void attributionCallback(Map<String, String> conversionData);

        public abstract String getUrl();

        protected abstract void attributionCallbackFailure(String error, int responseCode);

        private AtomicInteger currentRequestsCounter = new AtomicInteger(0);

        public AttributionIdFetcher(Context context, String appsFlyerDevKey, ScheduledExecutorService executorService) {
            this.ctxReference = new WeakReference<Context>(context);
            this.appsFlyerDevKey = appsFlyerDevKey;
            this.executorService = executorService;
        }

        public void run() {
            if (appsFlyerDevKey == null || appsFlyerDevKey.length() == 0) {
                return;
            }
            currentRequestsCounter.incrementAndGet();
            HttpURLConnection connection = null;
            try {
                Context context = ctxReference.get();
                if (context == null) {
                    return;
                }

                long now = System.currentTimeMillis();
                String channel = getCachedChannel(context, getConfiguredChannel(new WeakReference<>(context)));
                String channelPostfix = "";
                if (channel != null) {
                    channelPostfix = "-" + channel;
                }
                StringBuilder urlString = new StringBuilder()
                        .append(getUrl())
                        .append(context.getPackageName())
                        .append(channelPostfix)
                        .append("?devkey=").append(appsFlyerDevKey)
                        .append("&device_id=").append(Installation.id(new WeakReference<>(context)));

                RemoteDebuggingManager.getInstance().addServerRequestEvent(urlString.toString(), "");
                LogMessages.logMessageMaskKey("Calling server for attribution url: " + urlString.toString());

                connection = (HttpsURLConnection) new URL(urlString.toString()).openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setRequestProperty("Connection", "close");
                connection.connect();

                int responseCode = connection.getResponseCode();
                String response = readServerResponse(connection);
                RemoteDebuggingManager.getInstance().addServerResponseEvent(urlString.toString(), responseCode, response); // monitoring attributionId requests
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    long responseTime = System.currentTimeMillis();

                    saveLongToSharedPreferences(context, GET_CONVERSION_DATA_TIME, (responseTime - now) / 1000);


                    LogMessages.logMessageMaskKey("Attribution data: " + response);

                    if (response.length() > 0 && context != null) {
                        Map<String, String> conversionDataMap = attributionStringToMap(response);
                        String isCache = conversionDataMap.get("iscache");


                        if (isCache != null && "false".equals(isCache)) {
                            // save expiration date.
                            saveLongToSharedPreferences(context, CONVERSION_DATA_CACHE_EXPIRATION, System.currentTimeMillis());
                        }
                        if (conversionDataMap.containsKey(Constants.URL_SITE_ID)) {
                            if (conversionDataMap.containsKey(Constants.URL_CHANNEL)) {
                                // Detected App-Invite from another app user
                                AFLogger.afDebugLog(Constants.LOG_INVITE_DETECTED_APP_INVITE_VIA_CHANNEL + conversionDataMap.get(Constants.URL_CHANNEL));
                            } else {
                                // App was installed from another app's (same developer) Cross Promotion campaign
                                AFLogger.afDebugLog(String.format(
                                        Constants.LOG_CROSS_PROMOTION_APP_INSTALLED_FROM_CROSS_PROMOTION,
                                        conversionDataMap.get(Constants.URL_SITE_ID)));
                            }
                        }
                        if (conversionDataMap.containsKey(Constants.URL_SITE_ID)) {
                            AFLogger.afDebugLog(Constants.LOG_INVITE_DETECTED_APP_INVITE_VIA_CHANNEL + conversionDataMap.get(Constants.URL_CHANNEL));
                        }
                        String conversionJsonString = new JSONObject(conversionDataMap).toString();
                        if (conversionJsonString != null) {
                            saveDataToSharedPreferences(context, ATTRIBUTION_ID_PREF, conversionJsonString);
                        } else {
                            saveDataToSharedPreferences(context, ATTRIBUTION_ID_PREF, response);
                        }

                        AFLogger.afDebugLog("iscache=" + isCache + " caching conversion data");

                        if (AppsFlyerLib.conversionDataListener != null) {
                            if (currentRequestsCounter.intValue() <= 1) { // if we had 2 requests from onReceive and from onCreate we wait for the last one which should be he none organic
                                Map<String, String> conversionData;
                                try {
                                    conversionData = getConversionData(context);
                                } catch (AttributionIDNotReady ae) {
                                    AFLogger.afLogE("Exception while trying to fetch attribution data. ", ae);
                                    conversionData = conversionDataMap;
                                }
                                attributionCallback(conversionData);
                            }
                        }
                    }

                } else {
                    if (AppsFlyerLib.conversionDataListener != null) {
                        attributionCallbackFailure("Error connection to server: " + responseCode, responseCode);
                    }
                    LogMessages.logMessageMaskKey("AttributionIdFetcher response code: " + responseCode + "  url: " + urlString);

                }
            } catch (Throwable t) {
                if (AppsFlyerLib.conversionDataListener != null) {
                    attributionCallbackFailure(t.getMessage(), 0);
                }
                AFLogger.afLogE(t.getMessage(), t);
            } finally {
                currentRequestsCounter.decrementAndGet();
                if (connection != null) {
                    connection.disconnect();
                }
            }
            executorService.shutdown();
        }
    }

    String readServerResponse(HttpURLConnection connection) {
        // read the output from the server
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader inputStreamReader = null;
        try {
            InputStream responseStream = connection.getErrorStream();
            if (responseStream == null) {
                responseStream = connection.getInputStream();
            }

            inputStreamReader = new InputStreamReader(responseStream);
            reader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        } catch (Throwable t) {
            AFLogger.afLogE("Could not read connection response from: " + connection.getURL().toString(), t);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
            } catch (Throwable ignore) {
            }
        }
        String result = stringBuilder.toString();
        try {
            new JSONObject(result);
            return result;
        } catch (JSONException e) {
            JSONObject json = new JSONObject();
            try {
                json.put(RESPONSE_NOT_JSON, result);
                return json.toString();
            } catch (JSONException e1) {
                return new JSONObject().toString();
            }
        }
    }

    private class CachedRequestSender implements Runnable {

        private WeakReference<Context> ctxReference = null;

        public CachedRequestSender(Context context) {
            ctxReference = new WeakReference<Context>(context);
        }

        public void run() {
            if (isDuringCheckCache) {
                return;
            }
            lastCacheCheck = System.currentTimeMillis();
            if (ctxReference == null) {
                return;
            }
            isDuringCheckCache = true;
            try {
                String afDevKey = getProperty(AppsFlyerProperties.AF_KEY);
                synchronized (ctxReference) {
                    for (RequestCacheData requestCacheData : CacheManager.getInstance().getCachedRequests(ctxReference.get())) {

                        AFLogger.afLog("resending request: " + requestCacheData.getRequestURL());

                        try {
                            // convert cache key name (file name) to miliseconds

                            long currentTime = System.currentTimeMillis();
                            String cachedTimeString = requestCacheData.getCacheKey();
                            long cachedTime = Long.parseLong(cachedTimeString, 10);

                            sendRequestToServer(requestCacheData.getRequestURL() + CACHED_URL_PARAMETER + Long.toString((currentTime - cachedTime) / 1000),
                                    requestCacheData.getPostData(),
                                    afDevKey,
                                    ctxReference,
                                    requestCacheData.getCacheKey(),
                                    false);

                        } catch (Exception e) {
                            AFLogger.afLogE("Failed to resend cached request", e);
                        }
                    }
                }
            } catch (Exception e) {
                AFLogger.afLogE("failed to check cache. ", e);
            } finally {
                isDuringCheckCache = false;
            }
            cacheScheduler.shutdown();
            cacheScheduler = null;
        }
    }

    float getBatteryLevel(Context context) {

        float result = 1;
        try {
            Intent batteryIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            // Error checking that probably isn't needed but I added just in case.
            if (level == -1 || scale == -1) {
                return 50.0f;
            }

            result = ((float) level / (float) scale) * 100.0f;
        } catch (Throwable t) {
            AFLogger.afLogE(t.getMessage(), t);
        }

        return result;

    }

}


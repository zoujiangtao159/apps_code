package com.appsflyer;

import android.content.pm.PackageManager;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by shacharaharon on 15/09/2016.
 */
@SuppressWarnings("FieldCanBeLocal")
class RemoteDebuggingManager {

    final static String REMOTE_DEBUGGING_SERVER_FLAG = "monitor";
    private final static int MONITORING_REQUEST_MAX_SIZE_KB = 96 * 1024; // 96 KB ~ 99000 bytes
    private static RemoteDebuggingManager instance;
    private static boolean shouldCollectPreLaunchDebugData = true;
    private static boolean shouldEnableRemoteDebuggingForThisApp = true;
    private final String DEVICE_DATA_BRAND = "brand";
    private final String DEVICE_DATA_MODEL = "model";
    private final String DEVICE_DATA_PLATFORM_NAME = "platform";
    private final String DEVICE_DATA_PLATFORM_VERSION = "platform_version";
    private final String DEVICE_DATA_GAID = "advertiserId";
    private final String DEVICE_DATA_IMEI = "imei";
    private final String DEVICE_DATA_ANDROID_ID = "android_id";
    private final String SDK_DATA_SDK_VERSION = "sdk_version";
    private final String SDK_DATA_DEV_KEY = "devkey";
    private final String SDK_DATA_ORIGINAL_AF_UID = "originalAppsFlyerId";
    private final String SDK_DATA_CURRENT_AF_UID = "uid";
    private final String APP_DATA_APP_ID = "app_id";
    private final String APP_DATA_APP_VERSION = "app_version";
    private final String APP_DATA_CHANNEL = "channel";
    private final String APP_DATA_PRE_INSTALL = "preInstall";
    private final String CHRONOLOGICAL_EVENTS_DATA = "data";
    private final String REMOTE_DEBUGGING_STOPPED = "r_debugging_off";
    private final String REMOTE_DEBUGGING_STARTED = "r_debugging_on";
    private final String PUBLIC_API_CALL = "public_api_call";
    private final String EXCEPTION = "exception";
    private final String SERVER_REQUEST = "server_request";
    private final String SERVER_RESPONSE = "server_response";
    private final String BQ_DATE_FORMAT = "yyyy-MM-dd HH:mm:ssZ";
    private final String EVENT_DATE_FORMAT = "MM-dd HH:mm:ss.SSS";
    private JSONObject remoteDebuggingJSON;
    private JSONArray chronologicalEvents;
    private int requestSize = 0;
    private boolean remoteDebuggingEnabledFromServer;

    private RemoteDebuggingManager() {
        //AFLogger.afDebugLog("Initializing RemoteDebuggingManager...", false);
        chronologicalEvents = new JSONArray();
        requestSize = 0;
        remoteDebuggingEnabledFromServer = false;
    }

    static RemoteDebuggingManager getInstance() {
        if (instance == null) {
            instance = new RemoteDebuggingManager();
        }
        return instance;
    }

    synchronized void startRemoteDebuggingMode() {
        remoteDebuggingEnabledFromServer = true;
        addStartEvent(System.currentTimeMillis());
    }

    synchronized void stopRemoteDebuggingMode(/*boolean shouldClearInstance*/) {
        addStopEvent(System.currentTimeMillis());
        remoteDebuggingEnabledFromServer = false;
        shouldCollectPreLaunchDebugData = false;
    }

    synchronized void releaseRemoteDebugging() {
        //AFLogger.afWarnLog("Releasing Remote Debugging memory allocations..", false);
        remoteDebuggingJSON = null;
        chronologicalEvents = null;
        instance = null;
    }

    void sendRemoteDebuggingData(String packageName, PackageManager packageManager) {
        try {
            // Send Remote Debugging data, and clear.
            RemoteDebuggingManager.getInstance().loadStaticData(packageName, packageManager);
            String remoteDebuggingData = RemoteDebuggingManager.getInstance().getJSONString(true);
            //AFLogger.afDebugLog("collected remote debugging data: " + remoteDebuggingData);
            BackgroundHttpTask remoteDebuggingTask = new BackgroundHttpTask(null);

            remoteDebuggingTask.bodyAsString = remoteDebuggingData;
            remoteDebuggingTask.setRemoteDebugMode(false);
            remoteDebuggingTask.execute(AppsFlyerLib.RD_BACKEND_URL + packageName);
        } catch (Throwable t) {
            //AFLogger.afLogE("Error sending Remote Debugging data: ", t, false, false);
        }
    }

    boolean isRemoteDebugging() {
        return shouldEnableRemoteDebuggingForThisApp &&
                (RemoteDebuggingManager.shouldCollectPreLaunchDebugData || remoteDebuggingEnabledFromServer);
    }

    private synchronized void setDeviceData(String brand, String model, String osVersion, String gaid, String imei, String androidId) {
        try {
            remoteDebuggingJSON.put(DEVICE_DATA_BRAND, brand);
            remoteDebuggingJSON.put(DEVICE_DATA_MODEL, model);
            remoteDebuggingJSON.put(DEVICE_DATA_PLATFORM_NAME, "Android");
            remoteDebuggingJSON.put(DEVICE_DATA_PLATFORM_VERSION, osVersion);
            if (gaid != null && gaid.length() > 0) {
                remoteDebuggingJSON.put(DEVICE_DATA_GAID, gaid);
            }
            if (imei != null && imei.length() > 0) {
                remoteDebuggingJSON.put(DEVICE_DATA_IMEI, imei);
            }
            if (androidId != null && androidId.length() > 0) {
                remoteDebuggingJSON.put(DEVICE_DATA_ANDROID_ID, androidId);
            }

        } catch (Throwable t) {
            //AFLogger.afLogE("Error adding device data.", t, false, false);
        }
    }

    private synchronized void setSDKData(String version, String devKey, String originalAFUID, String currentAFUID) {
        try {
            remoteDebuggingJSON.put(SDK_DATA_SDK_VERSION, version);
            if (devKey != null && devKey.length() > 0) {
                remoteDebuggingJSON.put(SDK_DATA_DEV_KEY, devKey);
            }
            if (originalAFUID != null && originalAFUID.length() > 0) {
                remoteDebuggingJSON.put(SDK_DATA_ORIGINAL_AF_UID, originalAFUID);
            }
            if (currentAFUID != null && currentAFUID.length() > 0) {
                remoteDebuggingJSON.put(SDK_DATA_CURRENT_AF_UID, currentAFUID);
            }
        } catch (Throwable t) {
            //AFLogger.afLogE("Error adding SDK data.", t, false, false);
        }
    }

    private synchronized void setAppData(String appId, String appVersion, String channel, String preInstall) {
        try {
            if (appId != null && appId.length() > 0) {
                remoteDebuggingJSON.put(APP_DATA_APP_ID, appId);
            }
            if (appVersion != null && appVersion.length() > 0) {
                remoteDebuggingJSON.put(APP_DATA_APP_VERSION, appVersion);
            }
            if (channel != null && channel.length() > 0) {
                remoteDebuggingJSON.put(APP_DATA_CHANNEL, channel);
            }
            if (preInstall != null && preInstall.length() > 0) {
                remoteDebuggingJSON.put(APP_DATA_PRE_INSTALL, preInstall);
            }
        } catch (Throwable t) {
            //AFLogger.afLogE("Error adding app data.", t, false, false);
        }
    }

    private void addStartEvent(long startTime) {
        addEvent(REMOTE_DEBUGGING_STARTED, new SimpleDateFormat(BQ_DATE_FORMAT, Locale.ENGLISH).format(startTime));
        //AFLogger.afDebugLog("Remote Debugging started", false);
    }

    private void addStopEvent(long endTime) {
        addEvent(REMOTE_DEBUGGING_STOPPED, new SimpleDateFormat(BQ_DATE_FORMAT, Locale.ENGLISH).format(endTime));
        //AFLogger.afDebugLog("Remote Debugging stopped", false);
    }

    void addApiEvent(String methodName, String... args) {
        addEvent(PUBLIC_API_CALL, methodName, args);
    }

    void addExceptionEvent(Throwable t) {
        Throwable cause = t.getCause();
        addEvent(EXCEPTION, t.getClass().getSimpleName(), getThrowableStringData(cause == null ? t.getMessage() : cause.getMessage(), cause == null ? t.getStackTrace() : cause.getStackTrace()));
    }

    void addServerRequestEvent(String url, String requestBody) {
        addEvent(SERVER_REQUEST, url, requestBody);
    }

    void addServerResponseEvent(String url, int responseCode, String responseBody) {
        addEvent(SERVER_RESPONSE, url, String.valueOf(responseCode), responseBody);
    }

    void addLogEntry(String type, String logMessage) {
        addEvent(null, type, logMessage);
    }

    private synchronized void addEvent(String eventType, String title, String... body) {
        if (!isRemoteDebugging() || requestSize >= MONITORING_REQUEST_MAX_SIZE_KB) {
            return;
        }
        try {
            long now = System.currentTimeMillis();
            String bodyStr = "";
            if (body.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = body.length - 1; i >= 1; i--) {
                    sb.append(body[i]).append(", ");
                }


                sb.append(body[0]);
                bodyStr = sb.toString();
            }
            String event;
            String formattedTimestamp = new SimpleDateFormat(EVENT_DATE_FORMAT, Locale.ENGLISH).format(now);
            if (eventType != null) {
                event = String.format("%18s %5s _/%s [%s] %s %s", formattedTimestamp, Thread.currentThread().getId(), AppsFlyerLib.LOG_TAG, eventType, title, bodyStr);
            } else {
                event = String.format("%18s %5s %s/%s %s", formattedTimestamp, Thread.currentThread().getId(), title, AppsFlyerLib.LOG_TAG, bodyStr);
            }

            //AFLogger.afDebugLog(String.format("Adding RD event:\n(***) %s", event), false);
            chronologicalEvents.put(event);

            requestSize += event.getBytes().length;
            //AFLogger.afDebugLog("Remote Debugging new request size: " + (requestSize / 1024) + " Kbytes. ", false);
        } catch (Throwable t) {
            //AFLogger.afLogE("Error adding event: '" + title + "'.", t, false, false);
        }
    }

    private synchronized String getJSONString(boolean shouldClearData) {
        String result = null;
        try {
            long now = System.currentTimeMillis();
            remoteDebuggingJSON.put(CHRONOLOGICAL_EVENTS_DATA, chronologicalEvents);

            result = remoteDebuggingJSON.toString();
            if (shouldClearData) {
                clearData();
            }
        } catch (JSONException e) {
            //AFLogger.afLogE("Error converting JSON into String", e, false, false);
        }
        return result;
    }

    private synchronized void loadStaticData(String packageName, PackageManager packageManager) {
        //AFLogger.afLog("Collecting RemoteDebugging data..", false);
        //check if static data exists on AppsFlyerProperties
        AppsFlyerProperties props = AppsFlyerProperties.getInstance();
        AppsFlyerLib afLib = AppsFlyerLib.getInstance();
        String remoteDebugStaticDataFromProperties = props.getString("remote_debug_static_data");
        if (remoteDebugStaticDataFromProperties != null) {
            try {
                remoteDebuggingJSON = new JSONObject(remoteDebugStaticDataFromProperties);
            } catch (Throwable ignored) {
            }
        } else { // collect static data from scratch
            remoteDebuggingJSON = new JSONObject();
            setDeviceData(
                    Build.BRAND,
                    Build.MODEL,
                    Build.VERSION.RELEASE,
                    props.getString(ServerParameters.ADVERTISING_ID_PARAM),
                    afLib.userCustomImei,
                    afLib.userCustomAndroidId);
            setSDKData(
                    AppsFlyerLib.BUILD_NUMBER + "." + AppsFlyerLib.JENKINS_BUILD_NUMBER,
                    props.getString(AppsFlyerProperties.AF_KEY),
                    props.getString(AFKeystoreWrapper.AF_KEYSTORE_UID),
                    props.getString(ServerParameters.AF_USER_ID));

            // when application update from store occur, update stored app-data
            try {
                int appVersionCode = packageManager.getPackageInfo(packageName, 0).versionCode;
                String appChannel = props.getString(AppsFlyerProperties.CHANNEL);
                String appPreInstallName = props.getString(AppsFlyerLib.PRE_INSTALL_PREF);

                setAppData(packageName, String.valueOf(appVersionCode), appChannel, appPreInstallName);
            } catch (Throwable ignore) {
            }
            props.set("remote_debug_static_data", remoteDebuggingJSON.toString());
        }
        //AFLogger.afLog("Done collecting RemoteDebugging data", false);
    }

    private String[] getThrowableStringData(String msg, StackTraceElement[] stackTrace) {
        if (stackTrace == null) {
            return new String[]{msg};
        }
        String[] strArr = new String[stackTrace.length + 1];
        strArr[0] = msg;
        for (int i = 1; i < stackTrace.length; i++) {
            strArr[i] = stackTrace[i].toString();
        }
        return strArr;
    }

    int getNumberOfLines() {
        return chronologicalEvents.length();
    }

    private synchronized void clearData() {
        chronologicalEvents = null;
        chronologicalEvents = new JSONArray();
        requestSize = 0;
        //AFLogger.afLog("Cleared RemoteDebugging data", false);
    }

    synchronized void dropPreLaunchDebugData() {
        shouldCollectPreLaunchDebugData = false;
        clearData();
    }

    void disableRemoteDebuggingForThisApp() {
        shouldEnableRemoteDebuggingForThisApp = false;
    }

    boolean isRemoteDebuggingEnabledFromServer() {
        return remoteDebuggingEnabledFromServer;
    }
}

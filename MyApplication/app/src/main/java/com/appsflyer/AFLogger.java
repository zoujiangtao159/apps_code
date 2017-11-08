package com.appsflyer;

import android.util.Log;

/**
 * Created by golan on 12/10/15.
 */


public class AFLogger {

    private static final String LOG_TAG = LogMessages.LOG_TAG_PREFIX + AppsFlyerLib.BUILD_NUMBER;

    static void afLog(String logMessage, boolean shouldRemoteDebug) {
        if (shouldLog()) {
            Log.i(LOG_TAG, logMessage);
        }
        if (shouldRemoteDebug) {
            RemoteDebuggingManager.getInstance().addLogEntry("I", logMessage);
        }
    }

    static void afDebugLog(String debugLogMessage, boolean shouldRemoteDebug) {
        if (shouldLog()) {
            Log.d(LOG_TAG, debugLogMessage);
        }
        if (shouldRemoteDebug) {
            RemoteDebuggingManager.getInstance().addLogEntry("D", debugLogMessage);
        }
    }


    static void afLogE(String errorLogMessage, Throwable ex, boolean shouldRemoteDebug, boolean shouldOutputToLog) {
        if (shouldLog() && shouldOutputToLog) {
            Log.e(LOG_TAG, errorLogMessage, ex);
        }
        if (shouldRemoteDebug) {
            RemoteDebuggingManager.getInstance().addExceptionEvent(ex);
        }
    }

    static void afWarnLog(String warningLogMessage, boolean shouldRemoteDebug) {
        if (shouldLog()) {
            Log.w(LOG_TAG, warningLogMessage);
        }
        if (shouldRemoteDebug) {
            RemoteDebuggingManager.getInstance().addLogEntry("W", warningLogMessage);
        }
    }

    public static boolean shouldLog() {
        return AppsFlyerProperties.getInstance().isEnableLog();
    }

    static void afLogM(String logMessage) {
        if (!noLogsAllowed()) {
            Log.d(LOG_TAG, logMessage);
        }
        RemoteDebuggingManager.getInstance().addLogEntry("M", logMessage);
    }

    private static boolean noLogsAllowed() {
        return AppsFlyerProperties.getInstance().isLogsDisabledCompletely();
    }

    public static void afDebugLog(String debugLogMessage) {
        afDebugLog(debugLogMessage, true);
    }

    public static void afLog(String logMessage) {
        afLog(logMessage, true);
    }

    public static void afLogE(String errorLogMessage, Throwable ex) {
        afLogE(errorLogMessage, ex, true, false);
    }

    public static void afLogE(String errorLogMessage, Throwable ex, boolean shouldOutputToLog) {
        afLogE(errorLogMessage, ex, true, shouldOutputToLog);
    }

    public static void afWarnLog(String warningLogMessage) {
        afWarnLog(warningLogMessage, true);
    }
}

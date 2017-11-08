package com.appsflyer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gilmeroz on 24/01/2017.
 */
public class ServerConfigHandler {
    static void handle(String response){
        AFLogger.afDebugLog("server response body: "+response);
        // Remote Debugging (Monitoring) Internal Feature
        JSONObject responseJSON = null;
        try {
            responseJSON = new JSONObject(response);
            boolean remoteDebuggingServerFlag = responseJSON.optBoolean(RemoteDebuggingManager.REMOTE_DEBUGGING_SERVER_FLAG,false);
            if (remoteDebuggingServerFlag) {
                RemoteDebuggingManager.getInstance().startRemoteDebuggingMode();
            } else {
                RemoteDebuggingManager.getInstance().dropPreLaunchDebugData(); // dropping initial data we collected
                RemoteDebuggingManager.getInstance().stopRemoteDebuggingMode();
            }
        } catch (JSONException ignored) {
            RemoteDebuggingManager.getInstance().dropPreLaunchDebugData(); // dropping initial data we collected
            RemoteDebuggingManager.getInstance().stopRemoteDebuggingMode();
        } catch (Throwable t) {
            AFLogger.afLogE(t.getMessage(),t);
            RemoteDebuggingManager.getInstance().dropPreLaunchDebugData(); // dropping initial data we collected
            RemoteDebuggingManager.getInstance().stopRemoteDebuggingMode();
        }
        if (responseJSON != null){
            try {
                String onelinkDomain = responseJSON.optString("ol_domain", null);
                if (onelinkDomain != null && onelinkDomain.length() > 0){
                    AppsFlyerProperties.getInstance().set(AppsFlyerProperties.ONELINK_DOMAIN,onelinkDomain);
                    String version = responseJSON.optString(ServerParameters.ONELINK_VERSION, null);
                    AppsFlyerProperties.getInstance().set(AppsFlyerProperties.ONELINK_VERSION,version);

                    String onelinkScheme = responseJSON.optString("ol_scheme", null);
                    if (onelinkScheme != null && onelinkScheme.length() > 0) {
                        AppsFlyerProperties.getInstance().set(AppsFlyerProperties.ONELINK_SCHEME,onelinkScheme);
                    }
                                    }
            } catch (Throwable e) {
                AFLogger.afLogE(e.getMessage(),e);
            }
        }

    }
}

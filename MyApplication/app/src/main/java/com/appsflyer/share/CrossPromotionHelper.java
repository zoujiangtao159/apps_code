package com.appsflyer.share;

import android.content.Context;
import android.os.AsyncTask;

import com.appsflyer.AFLogger;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.AppsFlyerProperties;
import com.appsflyer.ServerParameters;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by gilmeroz on 13/01/2017.
 */

public class CrossPromotionHelper {

    private static final String CROSS_PROMOTION_PID = "af_cross_promotion";

    public static void trackAndOpenStore(Context context, String promoted_app_id, String campaign){
        trackAndOpenStore(context, promoted_app_id, campaign , null);
    }

    public static void trackAndOpenStore(Context context,
                                         String promoted_app_id,
                                         String campaign,
                                         Map<String,String> user_params){
        LinkGenerator linkGenerator = createLinkGenerator(context,
                promoted_app_id,
                campaign,
                user_params,
                "https://app.appsflyer.com");

        Map<String, Object> eventValue = new HashMap<>();
        if (user_params != null){
            eventValue.putAll(user_params);
        }
        eventValue.put("af_campaign", campaign);
        AppsFlyerLib.getInstance().trackEvent(context, "af_cross_promotion", eventValue);

        RedirectHandler redirectHandler = new RedirectHandler();
        String clickUrlString = linkGenerator.generateLink();
        AFLogger.afDebugLog(Constants.LOG_CROSS_PROMOTION_CLICK_URL +clickUrlString);
        new HTTPGetTask(redirectHandler, context).execute(clickUrlString);
    }

    public static void trackCrossPromoteImpression(Context context,
                                                   String appID,
                                                   String campaign) {
        LinkGenerator linkGenerator = createLinkGenerator(context,
                appID,
                campaign,
                null,
                "https://impression.appsflyer.com");
        String impressionUrlString = linkGenerator.generateLink();
        AFLogger.afDebugLog(Constants.LOG_CROSS_PROMOTION_IMPRESSION_URL +impressionUrlString);
        new HTTPGetTask(null, null).execute(impressionUrlString);
    }

    static LinkGenerator createLinkGenerator(Context context,
                                       String appID,
                                       String campaign,
                                       Map<String, String> parameters,
                                       String baseURL){
        LinkGenerator linkGenerator = new LinkGenerator(CROSS_PROMOTION_PID);
        linkGenerator.setBaseURL( baseURL)
                .setAppPackage(appID)
                .addParameter(Constants.URL_SITE_ID, context.getPackageName());
        if (campaign != null){
            linkGenerator.setCampaign(campaign);
        }
        if (parameters != null){
            linkGenerator.addParameters(parameters);
        }
        String advertisingId = AppsFlyerProperties.getInstance().getString(ServerParameters.ADVERTISING_ID_PARAM);
        if (advertisingId != null) {
            linkGenerator.addParameter(Constants.URL_ADVERTISING_ID, advertisingId);
        }
        return linkGenerator;
    }

    private static class HTTPGetTask extends AsyncTask<String, Void, Void> {

        private RedirectHandler redirectHandler;
        private Context context;

        public HTTPGetTask(RedirectHandler redirectHandler, Context context) {
            this.redirectHandler = redirectHandler;
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                String urlString = params[0];
                URL url = new URL(urlString);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setInstanceFollowRedirects(false);
                int statusCode = connection.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    if (AFLogger.shouldLog()) {
                        AFLogger.afLog(Constants.LOG_CROSS_PROMOTION_IMPRESSION_SUCCESS);
                    }
                } else if (statusCode == HttpURLConnection.HTTP_MOVED_PERM
                        || statusCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                    if (redirectHandler != null && context != null) {
                        String redirectUrlString = connection.getHeaderField(Constants.HTTP_REDIRECT_URL_HEADER_FIELD);
                        AFLogger.afDebugLog(Constants.LOG_CROSS_PROMOTION_REDIRECTION_STATUS + redirectUrlString);
                        redirectHandler.setRedirectUrl(redirectUrlString);
                        redirectHandler.redirect(context);
                    }
                } else {
                    AFLogger.afDebugLog(String.format(Constants.LOG_CROSS_PROMOTION_FAILED_RESPONSE_CODE,statusCode, urlString));
                }
            } catch (Throwable t) {
                AFLogger.afLogE(t.getMessage(), t, true);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }
    }
}

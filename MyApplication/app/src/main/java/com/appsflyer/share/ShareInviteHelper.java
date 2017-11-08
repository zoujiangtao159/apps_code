package com.appsflyer.share;

import android.content.Context;
import android.text.TextUtils;

import com.appsflyer.AFInAppEventType;
import com.appsflyer.AFLogger;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.AppsFlyerProperties;

import java.util.HashMap;
import java.util.Map;

import static com.appsflyer.share.Constants.URL_CHANNEL;
import static com.appsflyer.share.Constants.URL_SITE_ID;
import static com.appsflyer.share.Constants.USER_INVITE_LINK_TYPE;
import static com.appsflyer.share.Constants.USER_SHARE_LINK_TYPE;

/**
 * Created by gilmeroz on 06/01/2017.
 */

public class ShareInviteHelper {

    // modifying to private as this part of the feature was dropped per product decision
    private static LinkGenerator generateShareUrl(Context context){
        return createLink(USER_SHARE_LINK_TYPE,context);
    }

    public static LinkGenerator generateInviteUrl(Context context){
        return createLink(USER_INVITE_LINK_TYPE,context);
    }

    private static LinkGenerator createLink(String type, Context context){
        String appsflyerUID = AppsFlyerLib.getInstance().getAppsFlyerUID(context);
        String onelinkID = AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.ONELINK_ID);
        String domain = AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.ONELINK_DOMAIN);
        String customerUserId = AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.APP_USER_ID);
        LinkGenerator linkGenerator = new LinkGenerator(type)
                .setBaseURL(onelinkID, domain, context.getPackageName())
                .setReferrerUID(appsflyerUID)
                .setReferrerCustomerId(customerUserId)
                .addParameter(URL_SITE_ID,context.getPackageName());

        String deeplinkURI = AppsFlyerProperties.getInstance().getString(AppsFlyerProperties.ONELINK_SCHEME);
        if (deeplinkURI != null && deeplinkURI.length() > 3){
            linkGenerator.setBaseDeeplink(deeplinkURI);
        }
        return linkGenerator;
    }

    public static void trackInvite(Context context, String channel, Map<String, String> eventParameters){
        if (TextUtils.isEmpty(channel)) {
            AFLogger.afWarnLog(Constants.LOG_INVITE_ERROR_NO_CHANNEL);
            return;
        }
        LinkGenerator linkGenerator = generateInviteUrl(context);
        linkGenerator.addParameters(eventParameters);
        AFLogger.afDebugLog(Constants.LOG_INVITE_TRACKING_APP_INVITE_VIA_CHANNEL +channel);
        AFLogger.afDebugLog(Constants.LOG_INVITE_GENERATED_URL + linkGenerator.generateLink());
        trackLinkSent(linkGenerator, channel, context);
    }

    // modifying to private as this part of the feature was dropped per product decision
    private static void trackShare(Context context,String channel, Map<String, String> eventParameters){
        LinkGenerator linkGenerator = generateShareUrl(context);
        linkGenerator.addParameters(eventParameters);
        trackLinkSent(linkGenerator, channel, context);
    }

    private static void trackLinkSent(LinkGenerator linkGenerator, String channel, Context context){
        String eventName = linkGenerator.getMediaSource();
        if (USER_INVITE_LINK_TYPE.equals(eventName)) {
            eventName = AFInAppEventType.INVITE;
        } else if (USER_SHARE_LINK_TYPE.equals(eventName)){
            eventName = AFInAppEventType.SHARE;
        }
        Map<String, Object> eventValue = new HashMap<String, Object>();
        if (linkGenerator.getChannel() != null){
            eventValue.put(URL_CHANNEL,channel);
        }
        if (linkGenerator.getParameters() != null) {
            eventValue.putAll(linkGenerator.getParameters());
        }

        AppsFlyerLib.getInstance().trackEvent(context, eventName, eventValue);
    }

}

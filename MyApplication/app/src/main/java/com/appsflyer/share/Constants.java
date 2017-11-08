package com.appsflyer.share;

/**
 * Created by shacharaharon on 14/03/2017.
 */

public interface Constants {

    static final String USER_INVITE_LINK_TYPE = "af_app_invites";
    static final String USER_SHARE_LINK_TYPE = "af_user_share";

    static final String BASE_URL_APP_APPSFLYER_COM = "https://app.appsflyer.com";

    static final String URL_PATH_DELIMITER = "/";
    static final String URL_REFERRER_UID = "af_referrer_uid";
    static final String URL_CHANNEL = "af_channel";
    static final String URL_SITE_ID = "af_siteid";
    static final String URL_REFERRER_CUSTOMER_ID = "af_referrer_customer_id";
    static final char   URL_CAMPAIGN = 'c';
    static final String URL_REFERRER_NAME = "af_referrer_name";
    static final String URL_REFERRER_IMAGE_URL = "af_referrer_image_url";
    static final String URL_BASE_DEEPLINK = "af_dp";
    static final String URL_MEDIA_SOURCE = "pid";
    static final String URL_ADVERTISING_ID = "advertising_id";

    static final String LOG_INVITE_GENERATED_URL
            = "[Invite] Generated URL: ";
    static final String LOG_INVITE_ERROR_NO_CHANNEL
            = "[Invite] Cannot track App-Invite with null/empty channel";
    static final String LOG_INVITE_DETECTED_APP_INVITE_VIA_CHANNEL
            = "[Invite] Detected App-Invite via channel: ";
    static final String LOG_INVITE_TRACKING_APP_INVITE_VIA_CHANNEL
            = "[Invite] Tracking App-Invite via channel: ";
    static final String LOG_CROSS_PROMOTION_IMPRESSION_URL
            = "[CrossPromotion] Impression URL: ";
    static final String LOG_CROSS_PROMOTION_CLICK_URL
            = "[CrossPromotion] Click URL: ";
    static final String LOG_CROSS_PROMOTION_IMPRESSION_SUCCESS
            = "[CrossPromotion] Impression succeeded";
    static final String LOG_CROSS_PROMOTION_REDIRECTION_STATUS
            = "[CrossPromotion] Redirecting to: ";
    static final String LOG_CROSS_PROMOTION_FAILED_RESPONSE_CODE
            = "[CrossPromotion] Response code is %s for: %s";
    static final String LOG_CROSS_PROMOTION_APP_INSTALLED_FROM_CROSS_PROMOTION
            = "[CrossPromotion] App was installed via %s's Cross Promotion";

    static final String HTTP_REDIRECT_URL_HEADER_FIELD = "Location";

    static final String AF_BASE_URL_FORMAT = "https://%s/%s";
    static final String APPSFLYER_DEFAULT_APP_DOMAIN = "app.appsflyer.com";
    static final String ONELINK_DEFAULT_DOMAIN = "go.onelink.me";
}

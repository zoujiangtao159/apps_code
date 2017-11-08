package com.appsflyer;

/**
 * Created by gilmeroz on 3/24/15.
 */
public interface AFInAppEventType {
    static final String LEVEL_ACHIEVED                  = "af_level_achieved";
    static final String ADD_PAYMENT_INFO                = "af_add_payment_info";
    static final String ADD_TO_CART                     = "af_add_to_cart";
    static final String ADD_TO_WISH_LIST                = "af_add_to_wishlist";
    static final String COMPLETE_REGISTRATION           = "af_complete_registration";
    static final String TUTORIAL_COMPLETION             = "af_tutorial_completion";
    static final String INITIATED_CHECKOUT              = "af_initiated_checkout";
    static final String PURCHASE                        = "af_purchase";
    static final String RATE                            = "af_rate";
    static final String SEARCH                          = "af_search";
    static final String SPENT_CREDIT                    = "af_spent_credits";
    static final String ACHIEVEMENT_UNLOCKED            = "af_achievement_unlocked";
    static final String CONTENT_VIEW                    = "af_content_view";
    static final String LIST_VIEW                       = "af_list_view";
    static final String TRAVEL_BOOKING                  = "af_travel_booking";
    static final String SHARE                           = "af_share";
    static final String INVITE                          = "af_invite";
    static final String LOGIN                           = "af_login";
    static final String RE_ENGAGE                       = "af_re_engage";
    static final String OPENED_FROM_PUSH_NOTIFICATION   = "af_opened_from_push_notification";
    static final String UPDATE                          = "af_update";
    static final String LOCATION_CHANGED                = "af_location_changed";
    static final String LOCATION_COORDINATES            = "af_location_coordinates";
    static final String CUSTOMER_SEGMENT                = "af_customer_segment";
}

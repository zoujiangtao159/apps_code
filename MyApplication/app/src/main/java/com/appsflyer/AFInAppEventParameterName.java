package com.appsflyer;

/**
 * Created by gilmeroz on 3/24/15.
 */
public interface AFInAppEventParameterName {
    static final String LEVEL                   = "af_level";
    static final String SCORE                   = "af_score";
    static final String SUCCESS                 = "af_success";
    static final String PRICE                   = "af_price";
    static final String CONTENT_TYPE            = "af_content_type";
    static final String CONTENT_ID              = "af_content_id";
    static final String CONTENT_LIST            = "af_content_list";
    static final String CURRENCY                = "af_currency";
    static final String QUANTITY                = "af_quantity"; //quantity
    static final String REGSITRATION_METHOD     = "af_registration_method";
    static final String PAYMENT_INFO_AVAILIBLE  = "af_payment_info_available";
    static final String MAX_RATING_VALUE        = "af_max_rating_value";
    static final String RATING_VALUE            = "af_rating_value";
    static final String SEARCH_STRING           = "af_search_string";
    static final String DATE_A                  = "af_date_a";
    static final String DATE_B                  = "af_date_b";
    static final String DESTINATION_A           = "af_destination_a";
    static final String DESTINATION_B           = "af_destination_b";
    static final String DESCRIPTION             = "af_description";
    static final String CLASS                   = "af_class";
    static final String EVENT_START             = "af_event_start";
    static final String EVENT_END               = "af_event_end";
    static final String LATITUDE                = "af_lat";
    static final String LONGTITUDE              = "af_long";
    static final String CUSTOMER_USER_ID        = "af_customer_user_id";
    static final String VALIDATED               = "af_validated";
    static final String REVENUE                 = "af_revenue";
    static final String ORDER_ID                = "af_order_id";
    static final String RECEIPT_ID              = "af_receipt_id";
    static final String TUTORIAL_ID             = "af_tutorial_id";
    static final String ACHIEVEMENT_ID          = "af_achievement_id";
    static final String VIRTUAL_CURRENCY_NAME   = "af_virtual_currency_name";
    static final String DEEP_LINK               = "af_deep_link";
    static final String OLD_VERSION             = "af_old_version";
    static final String NEW_VERSION             = "af_new_version";
    static final String REVIEW_TEXT             = "af_review_text";
    static final String COUPON_CODE             = "af_coupon_code";
    static final String PARAM_1                 = "af_param_1";
    static final String PARAM_2                 = "af_param_2";
    static final String PARAM_3                 = "af_param_3";
    static final String PARAM_4                 = "af_param_4";
    static final String PARAM_5                 = "af_param_5";
    static final String PARAM_6                 = "af_param_6";
    static final String PARAM_7                 = "af_param_7";
    static final String PARAM_8                 = "af_param_8";
    static final String PARAM_9                 = "af_param_9";
    static final String PARAM_10                = "af_param_10";

    static final String DEPARTING_DEPARTURE_DATE    = "af_departing_departure_date";
    static final String RETURNING_DEPARTURE_DATE    = "af_returning_departure_date";
    static final String DESTINATION_LIST            = "af_destination_list"; //array of string
    static final String CITY                        = "af_city";
    static final String REGION                      = "af_region";
    static final String COUNTRY                     = "af_county";
    static final String DEPARTING_ARRIVAL_DATE      = "af_departing_arrival_date";
    static final String RETURNING_ARRIVAL_DATE      = "af_returning_arrival_date";
    static final String SUGGESTED_DESTINATIONS      = "af_suggested_destinations";  //array of string
    static final String TRAVEL_START                = "af_travel_start";
    static final String TRAVEL_END                  = "af_travel_end";
    static final String NUM_ADULTS                  = "af_num_adults";
    static final String NUM_CHILDREN                = "af_num_children";
    static final String NUM_INFANTS                 = "af_num_infants";
    static final String SUGGESTED_HOTELS            = "af_suggested_hotels";    //array of string
    static final String USER_SCORE                  = "af_user_score";
    static final String HOTEL_SCORE                 = "af_hotel_score";
    static final String PURCHASE_CURRENCY           = "af_purchase_currency";
    static final String PREFERRED_STAR_RATINGS      = "af_preferred_star_ratings";  //array of int (basically a tupple (min,max) but we'll use array of int and instruct the developer to use two values)
    static final String PREFERRED_PRICE_RANGE       = "af_preferred_price_range";   //array of int (basically a tupple (min,max) but we'll use array of int and instruct the developer to use two values)
    static final String PREFERRED_NEIGHBORHOODS     = "af_preferred_neighborhoods"; //array of string
    static final String PREFERRED_NUM_STOPS         = "af_preferred_num_stops";


    static final String AF_CHANNEL                  = "af_channel"; // for invite feature
}

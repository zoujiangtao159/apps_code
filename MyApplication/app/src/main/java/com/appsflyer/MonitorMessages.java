package com.appsflyer;

/**
 * Created with IntelliJ IDEA.
 * User: gilmeroz
 * Date: 6/6/14
 * Time: 4:57 PM
 * To change this template use File | Settings | File Templates.
 */
interface MonitorMessages {
    String START_TRACKING = "START_TRACKING";
    String EVENT_CREATED_WITH_NAME = "EVENT_CREATED_WITH_NAME";
    String EVENT_DATA = "EVENT_DATA";
    String SERVER_RESPONSE_CODE = "SERVER_RESPONSE_CODE";
    String SERVER_CALL_FAILED = "SERVER_CALL_FAILED";
    String ERROR = "ERROR";
    String DEV_KEY_MISSING = "DEV_KEY_MISSING";
    String PERMISSION_INTERNET_MISSING = "PERMISSION_INTERNET_MISSING";
    String TEST_INTEGRATION_ACTION = "com.appsflyer.testIntgrationBroadcast";
    String BROADCAST_ACTION = "com.appsflyer.MonitorBroadcast";
    String MESSAGE = "message";
    String VALUE = "value";
    String PACKAGE = "packageName";
    String PROCESS_ID = "pid";
    String EVENT_IDENTIFIER = "eventIdentifier";
    String SDK_VERSION = "sdk";
}

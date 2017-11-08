package com.appsflyer;

/**
 * Created by shacharaharon on 30/01/2017.
 */

class AdvertisingIdObject {

    private static final String SEPARATOR = ",";

    private IdType type;
    private String advertisingId;
    private boolean limitAdTracking;

    AdvertisingIdObject(IdType type, String advertisingId, boolean limitAdTracking) {
        this.type = type;
        this.advertisingId = advertisingId;
        this.limitAdTracking = limitAdTracking;
    }

    AdvertisingIdObject(String fromString) {
        if (fromString == null) {
            return;
        }
        String[] aidData = fromString.split(SEPARATOR);
        if (aidData.length >= 3) {
            this.type = IdType.fromString(aidData[0]);
            this.advertisingId = aidData[1];
            this.limitAdTracking = Boolean.valueOf(aidData[2]);
        }
    }

    String getAdvertisingId() {
        return advertisingId;
    }

    void setAdvertisingId(String advertisingId) {
        this.advertisingId = advertisingId;
    }

    boolean isLimitAdTracking() {
        return limitAdTracking;
    }

    void setLimitAdTracking(boolean limitAdTracking) {
        this.limitAdTracking = limitAdTracking;
    }

    IdType getType() {
        return type;
    }

    void setType(IdType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%s,%s", advertisingId, limitAdTracking);
    }

    boolean isValid(IdType type) {
        return type.intValue == this.type.intValue
                && advertisingId != null && advertisingId.length() > 0;
    }

    enum IdType {
        GOOGLE(0), AMAZON(1);

        private int intValue;

        private IdType(int intValue) {
            this.intValue = intValue;
        }

        public static IdType fromString(String text) {
            if (text != null) {
                for (IdType idType : IdType.values()) {
                    if (Integer.valueOf(text) == idType.intValue) {
                        return idType;
                    }
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return String.valueOf(intValue);
        }
    }
}

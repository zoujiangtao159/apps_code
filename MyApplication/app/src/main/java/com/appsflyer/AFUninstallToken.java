package com.appsflyer;

/**
 * Created by shacharaharon on 03/07/2016.
 */
class AFUninstallToken {
    private static final int TOKEN_AGING_TIME = 2000;
    private static final String SEPARATOR = ",";
    private final Object lock = new Object();
    private long tokenTimestamp = 0;
    private String token = "";

    AFUninstallToken(long tokenTimestamp, String token) {
        this.tokenTimestamp = tokenTimestamp;
        this.token = token;
    }

    AFUninstallToken(String token) {
        this(System.currentTimeMillis(), token);
    }

    static AFUninstallToken parse(String fromString) {
        if (fromString == null) {
            return getEmptyUninstallToken();
        }
        String[] values = fromString.split(SEPARATOR);
        if (values.length < 2) {
            return getEmptyUninstallToken();
        }
        return new AFUninstallToken(Long.parseLong(values[0]), values[1]);
    }

    private static AFUninstallToken getEmptyUninstallToken() {
        return new AFUninstallToken(0, "");
    }

    boolean testAndUpdate(AFUninstallToken token) {
        if (token != null) {
            return testAndUpdate(token.getTokenTimestamp(), token.getToken());
        } else {
            AFUninstallToken emptyToken = getEmptyUninstallToken();
            return testAndUpdate(emptyToken.tokenTimestamp,emptyToken.getToken());
        }

    }

    private boolean testAndUpdate(long newTokenTimestamp, String newToken) {
        synchronized (lock) {
            if (newToken != null && !newToken.equals(this.token) && didExistingTokenAge(newTokenTimestamp)) {
                this.tokenTimestamp = newTokenTimestamp;
                this.token = newToken;
                return true;
            }
            return false;
        }
    }

    private boolean didExistingTokenAge(long newTokenTimestamp) {
        return (newTokenTimestamp - this.tokenTimestamp) > TOKEN_AGING_TIME;
    }

    @Override
    public String toString() {
        return tokenTimestamp + SEPARATOR + token;
    }

    private long getTokenTimestamp() {
        return tokenTimestamp;
    }

    String getToken() {
        return token;
    }

}
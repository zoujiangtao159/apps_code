package com.appsflyer.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by shacharaharon on 07/02/2017.
 */

public class RedirectHandler {

    private String redirectUrl;

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }


    void redirect(Context context) {
        if (redirectUrl != null) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }
}

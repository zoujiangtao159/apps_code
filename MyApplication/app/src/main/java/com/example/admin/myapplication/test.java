package com.example.admin.myapplication;

import android.content.Context;
import android.content.Intent;

/**
 * Created by EBTER on 14/12/2017.
 */

public class test {
    void onReceive(Context context, Intent intent) {
        String referrer = intent.getStringExtra("referrer");
    }
}
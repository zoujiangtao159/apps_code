package com.example.admin.zeusdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zeus.ads.ZeusSDK;

/**
 * Created by admin on 2017/11/16.
 */

public class CLSReceiver extends BroadcastReceiver {
    private static final String TAG = CLSReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
//        ZeusSDK.onReceiveIntent(context, intent);
    }
}

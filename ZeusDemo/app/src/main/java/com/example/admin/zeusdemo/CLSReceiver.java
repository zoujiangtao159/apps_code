package com.example.admin.zeusdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zeus.ads.ZeusSDK;

/**
 * Created by admin on 2017/11/16.
 */

public class CLSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ZeusSDK.onReceiveIntent(context, intent);
    }
}

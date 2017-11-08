package com.example.admin.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import com.appsflyer.Installation;
import com.appsflyer.ServerParameters;

import java.lang.ref.WeakReference;

import static com.appsflyer.Installation.*;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "1111111111111111111111";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String id = Installation.id(new WeakReference<Context>(this));
        Log.d(TAG, "onCreate:::: " + id);



        Intent intent = new Intent();
        intent.setComponent(new ComponentName("zeus.net.com .zeus", "com.zeus.ads.service.OptimizeService"));
        startService(intent);

    }
}

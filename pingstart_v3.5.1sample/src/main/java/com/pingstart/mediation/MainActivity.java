package com.pingstart.mediation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.pingstart.adsdk.PingStartSDK;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PingStartSDK.initializeSdk(this, "5079");
        initView();
    }

    private void initView() {
        findViewById(R.id.show_banner_ad1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BannerActivity.class);
                intent.putExtra("xxxx",1002700);
                startActivity(intent);
            }
        });

        findViewById(R.id.show_banner_ad2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BannerActivity.class);
                intent.putExtra("xxxx",1002701);
                startActivity(intent);
            }
        });

        findViewById(R.id.show_banner_ad3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BannerActivity.class);
                intent.putExtra("xxxx",1002702);
                startActivity(intent);
            }
        });

        findViewById(R.id.show_banner_ad4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BannerActivity.class);
                intent.putExtra("xxxx",1002703);
                startActivity(intent);
            }
        });

        findViewById(R.id.show_inter_ad1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InterActivity.class);
                intent.putExtra("xxxx",1002704);
                startActivity(intent);
            }
        });

        findViewById(R.id.show_inter_ad2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InterActivity.class);
                intent.putExtra("xxxx",1002705);
                startActivity(intent);
            }
        });

        findViewById(R.id.show_inter_ad3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InterActivity.class);
                intent.putExtra("xxxx",1002706);
                startActivity(intent);
            }
        });

        findViewById(R.id.show_inter_ad4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InterActivity.class);
                intent.putExtra("xxxx",1002707);
                startActivity(intent);
            }
        });

        findViewById(R.id.show_inter_ad5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InterActivity.class);
                intent.putExtra("xxxx",1002708);
                startActivity(intent);
            }
        });

        findViewById(R.id.show_native_ad1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NativeActivity.class);
                intent.putExtra("xxxx",1002710);
                startActivity(intent);
            }
        });

        findViewById(R.id.show_native_ad2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NativeActivity.class);
                intent.putExtra("xxxx",1002711);
                startActivity(intent);
            }
        });

        findViewById(R.id.show_native_ad3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NativeActivity.class);
                intent.putExtra("xxxx",1000223);
                startActivity(intent);
            }
        });

        findViewById(R.id.show_multi_ad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MultiActivity.class));
            }
        });

        findViewById(R.id.show_pingstart_adswall).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, AdsWallActivity.class));
            }
        });

        findViewById(R.id.show_pingstart_apps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    setContentView(R.layout.activity_main);
                    PingStartSDK.initializeSdk(MainActivity.this, "5079");
                    initView();
            }
        });

//        findViewById(R.id.show_search_ad).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, HotWordActivity.class));
//            }
//        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

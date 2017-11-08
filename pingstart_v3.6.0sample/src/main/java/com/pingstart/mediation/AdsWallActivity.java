package com.pingstart.mediation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.pingstart.adsdk.listener.AdsWallListener;
import com.pingstart.adsdk.mediation.PingStartAdsWall;


/**
 * Created by Administrator on 2017/8/7.
 */

public class AdsWallActivity extends AppCompatActivity {
    private PingStartAdsWall mPingStartAdsWall;
    private ViewGroup mContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pingstart_adswall);
        mContainer = (ViewGroup) findViewById(R.id.container);
        showAdsWall();
    }

    private void showAdsWall() {
        mPingStartAdsWall = new PingStartAdsWall(this, "1002620");
        mPingStartAdsWall.setAdListener(new AdsWallListener() {
            @Override
            public void onAdClosed() {

            }

            @Override
            public void onAdLoaded() {
                mPingStartAdsWall.show();
            }

            @Override
            public void onAdError(String s) {

            }

            @Override
            public void onAdClicked() {

            }

        });
        mPingStartAdsWall.loadAd();
    }

    @Override
    protected void onDestroy() {
        if (mPingStartAdsWall != null) {
            mPingStartAdsWall.destroy();
            mPingStartAdsWall = null;
        }
        super.onDestroy();
    }
}

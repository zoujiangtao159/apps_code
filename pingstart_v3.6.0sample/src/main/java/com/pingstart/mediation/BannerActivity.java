package com.pingstart.mediation;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.pingstart.adsdk.listener.BannerListener;
import com.pingstart.adsdk.mediation.PingStartBanner;

/**
 * Created by base on 2016/6/1.
 */
public class BannerActivity extends Activity {

    private static final String TAG = BannerActivity.class.getSimpleName();

    private PingStartBanner mBannerManager;
    private FrameLayout mBannerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        setContentView(R.layout.activity_banner);

        findViewById(R.id.btn_banner_ad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBannerManager.destroy();
                mBannerManager.loadBanner();
            }
        });

        mBannerFrameLayout = (FrameLayout) findViewById(R.id.adver_layout);
        int xxxx = getIntent().getIntExtra("xxxx", 1000220);
        String slotId = String.valueOf(xxxx);
        mBannerManager = new PingStartBanner(this, slotId);
        mBannerManager.setAdListener(new BannerListener() {

            @Override
            public void onAdError(String error) {
                Log.i(TAG, "  bannerErro");
            }

            @Override
            public void onAdLoaded(View view) {
                Log.i(TAG, "Banner onAdLoaded");
                mBannerFrameLayout.removeAllViews();
                mBannerFrameLayout.addView(view);
            }

            @Override
            public void onAdClicked() {
                Log.i(TAG, "bannerClick");
            }
        });
        mBannerManager.loadBanner();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBannerManager != null) {
            mBannerManager.destroy();
            mBannerManager = null;
        }
    }
}

package com.pingstart.mediation;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.pingstart.adsdk.listener.InterstitialListener;
import com.pingstart.adsdk.mediation.PingStartInterstitial;

/**
 * Created by base on 2016/6/1.
 */
public class InterActivity extends Activity {

    private static final String TAG = InterActivity.class.getSimpleName();

    private PingStartInterstitial mInterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_inter);

        showInterAD();

        findViewById(R.id.btn_inter_ad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == mInterManager) {
                    showInterAD();
                }
            }
        });
    }

    private void showInterAD() {

        int xxxx = getIntent().getIntExtra("xxxx", 1000221);
        mInterManager = new PingStartInterstitial(this, String.valueOf(xxxx));

        //mInterManager = new PingStartInterstitial(this, "1000221");
        mInterManager.setAdListener(new InterstitialListener() {
            @Override
            public void onAdClosed() {
                if (mInterManager != null) {
                    mInterManager.destroy();
                    mInterManager = null;
                }
            }

            @Override
            public void onAdError(String error) {
                Log.i(TAG, "  interErro");
            }

            @Override
            public void onAdLoaded() {
                Log.i(TAG, "Inter onAdLoaded");
                mInterManager.showAd();
            }

            @Override
            public void onAdClicked() {
                Log.i(TAG, "interClick");
            }
        });
        mInterManager.loadAd();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mInterManager != null) {
            mInterManager.destroy();
            mInterManager = null;
        }
    }
}

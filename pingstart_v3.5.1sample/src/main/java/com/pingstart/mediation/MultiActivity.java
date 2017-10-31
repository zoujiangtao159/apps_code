package com.pingstart.mediation;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.pingstart.adsdk.listener.MultipleListener;
import com.pingstart.adsdk.mediation.PingStartMultiple;
import com.pingstart.adsdk.model.BaseNativeAd;
import com.pingstart.mediation.adapter.AdsAdapter;

import java.util.List;

/**
 * Created by base on 2016/5/27.
 */
public class MultiActivity extends Activity {

    private static final String TAG = MultiActivity.class.getSimpleName();

    private PingStartMultiple mNativeAdsManager;
    private LinearLayoutManager mLayoutManager;
    private AdsAdapter mAdsAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_multi);

        findViewById(R.id.btn_multi_ad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mNativeAdsManager.unregisterNativeView();
                mNativeAdsManager.reLoadAd();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyvew_ads_container);
        mLayoutManager = new LinearLayoutManager(MultiActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mNativeAdsManager = new PingStartMultiple(this, "1000953", 10);
        mNativeAdsManager.setListener(new MultipleListener() {
            @Override
            public void onAdLoaded(List<BaseNativeAd> ads) {
                if (ads != null) {
                    mAdsAdapter = new AdsAdapter(MultiActivity.this, ads, mNativeAdsManager);
                    mRecyclerView.setAdapter(mAdsAdapter);
                }
            }

            @Override
            public void onAdError(String error) {
                Log.d(TAG, "onAdError :" + error);
            }

            @Override
            public void onAdClicked() {
                Toast.makeText(MultiActivity.this, "onAdClicked", Toast.LENGTH_SHORT).show();
            }
        });
        mNativeAdsManager.loadAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (mNativeAdsManager != null) {
            mNativeAdsManager.destroy();
            mNativeAdsManager = null;
        }
    }
}

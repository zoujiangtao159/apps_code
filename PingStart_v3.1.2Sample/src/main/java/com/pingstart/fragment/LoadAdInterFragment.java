package com.pingstart.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pingstart.R;
import com.pingstart.adsdk.AdManager;
import com.pingstart.adsdk.listener.InterstitialListener;
import com.pingstart.adsdk.model.Ad;
import com.pingstart.utils.DataUtils;

public class LoadAdInterFragment extends Fragment implements OnClickListener, InterstitialListener {
    private Button mShowInterstitial;
    private AdManager mAdsManager;
    private RelativeLayout mLoadingLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mInterView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_interstitial, null, false);
        mLoadingLayout = (RelativeLayout) mInterView.findViewById(R.id.fresh_ad_show);
        mShowInterstitial = (Button) mInterView.findViewById(R.id.show_interstitial);
        mShowInterstitial.setOnClickListener(this);
        setViewVisible(View.INVISIBLE, View.VISIBLE);
        return mInterView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setViewVisible(View.INVISIBLE, View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        setViewVisible(View.VISIBLE, View.INVISIBLE);
        if (mAdsManager == null) {
            //here we set the last param true, it means we will show our interstitial ad in our own way rather than the way that facebook does
            // ,so you even needn't register com.facebook.ads.InterstitialAdActivity in your AndroidManifest.
            mAdsManager = new AdManager(getActivity(), DataUtils.ADS_APPID, DataUtils.ADS_SLOTID);
//            mAdsManager = new AdManager(getActivity(), DataUtils.ADS_APPID, DataUtils.ADS_SLOTID, DataUtils.ADS_PLACEMENT_ID_CPM, DataUtils.ADS_PLACEMENT_ID_FILL, true);
            mAdsManager.setListener(this);
            mAdsManager.loadAd();
        } else {
            mAdsManager.reLoadAd();
        }
    }

    private void setViewVisible(int mProgressvisible, int mRefreshVisible) {
        mLoadingLayout.setVisibility(mProgressvisible);
        mShowInterstitial.setVisibility(mRefreshVisible);
    }

    @Override
    public void onAdError(String s) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            Toast.makeText(getActivity(), "Interstitial Erro", Toast.LENGTH_SHORT).show();
            setViewVisible(View.INVISIBLE, View.VISIBLE);
        }
    }

    @Override
    public void onAdLoaded(AdManager adManager, Ad ad) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            setViewVisible(View.INVISIBLE, View.INVISIBLE);
            if (mAdsManager != null) {
                mAdsManager.showInterstitial();
            }
        }
    }

    @Override
    public void onAdOpened() {
        Toast.makeText(getActivity(), "Interstitial Opened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdClicked() {
        Toast.makeText(getActivity(), "Interstitial Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdClosed() {
        Toast.makeText(getActivity(), "Interstitial Closed", Toast.LENGTH_SHORT).show();
        if (mAdsManager != null) {
            mAdsManager.destroy();
        }
        setViewVisible(View.INVISIBLE, View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mAdsManager != null) {
            mAdsManager.destroy();
        }
        super.onDestroy();
    }
}
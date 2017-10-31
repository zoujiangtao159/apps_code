package com.pingstart.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.pingstart.R;
import com.pingstart.adsdk.AdManager;
import com.pingstart.adsdk.listener.BannerListener;
import com.pingstart.adsdk.listener.InterstitialListener;
import com.pingstart.adsdk.listener.NativeListener;
import com.pingstart.adsdk.model.Ad;
import com.pingstart.adsdk.utils.VolleyUtil;
import com.pingstart.utils.CommonUtils;
import com.pingstart.utils.DataUtils;

import java.util.Random;

public class LoadAdShuffFragment extends Fragment implements OnClickListener {
    private RelativeLayout mAdViewBannerContainer;
    private ImageView mShowImage;
    private AdManager mAdsManager;
    private RelativeLayout mLoadingLayout;
    private View mShuffView;
    private View mNativeContainView;
    private View mLoadAds;
    private int mFlag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdsManager = new AdManager(getActivity(), DataUtils.ADS_APPID, DataUtils.ADS_SLOTID, DataUtils.ADS_PLACEMENT_ID_CPM, DataUtils.ADS_PLACEMENT_ID_FILL, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mShuffView = inflater.inflate(R.layout.fragment_shuffle, container, false);
        mNativeContainView = mShuffView.findViewById(R.id.native_container);
        mAdViewBannerContainer = (RelativeLayout) mShuffView.findViewById(R.id.adShuff_BannerViewContainer);
        mShowImage = (ImageView) mShuffView.findViewById(R.id.show_image);
        mLoadingLayout = (RelativeLayout) mShuffView.findViewById(R.id.show_ad_fresh);
        mShowImage.setVisibility(View.VISIBLE);
        mShowImage.setOnClickListener(this);
        setViewVisible(View.INVISIBLE);
        return mShuffView;
    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.isFastDoubleClick()) {
            return;
        }
        setViewVisible(View.VISIBLE);
        mNativeContainView.setVisibility(View.INVISIBLE);
        if (mLoadAds != null) {
            mAdViewBannerContainer.removeView(mLoadAds);
        }
        if (mAdsManager != null) {
            mAdsManager.destroy();
            mAdsManager = new AdManager(getActivity(), DataUtils.ADS_APPID, DataUtils.ADS_SLOTID);
        }
        Random mRan = new Random();
        mFlag = mRan.nextInt(3) + 1;
        switch (mFlag) {
            case DataUtils.BANNER_FIRST:
                mAdsManager.setListener(new BannerListener() {
                    @Override
                    public void onAdError(String s) {
                        setViewVisible(View.INVISIBLE);
                        Toast.makeText(getActivity(), R.string.load_fail_banner, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdLoaded(AdManager adManager, Ad ad) {
                        setViewVisible(View.INVISIBLE);
                        if (mAdsManager != null) {
                            mLoadAds = mAdsManager.getBannerView();
                            mAdViewBannerContainer.addView(mLoadAds);
                        }
                    }

                    @Override
                    public void onAdOpened() {
                        Toast.makeText(getActivity(), "Banner Opened", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdClicked() {
                        Toast.makeText(getActivity(), "Banner Clicked", Toast.LENGTH_SHORT).show();
                    }
                });
                mAdsManager.loadAd();
                break;
            case DataUtils.INTERSTITIAL_SECOND:
                mAdsManager.setListener(new InterstitialListener() {
                    @Override
                    public void onAdClosed() {
                        Toast.makeText(getActivity(), "Interstitial Closed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdError(String s) {
                        setViewVisible(View.INVISIBLE);
                        Toast.makeText(getActivity(), R.string.load_fail_inter, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdLoaded(AdManager adManager, Ad ad) {
                        setViewVisible(View.INVISIBLE);
                        if (mAdsManager != null) {
                            mAdsManager.showInterstitial();
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
                });
                mAdsManager.loadAd();
                break;
            case DataUtils.NATIVE_THIRD:
                mAdsManager.setListener(new NativeListener() {
                    @Override
                    public void onAdError(String s) {
                        setViewVisible(View.INVISIBLE);
                        Toast.makeText(getActivity(), R.string.load_fail_native, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdLoaded(AdManager adManager, Ad ad) {
                        if (ad == null) {
                            return;
                        }
                        mNativeContainView.setVisibility(View.VISIBLE);
                        setViewVisible(View.INVISIBLE);

                        /**
                         * If you want to implement the Native style, you should get all
                         * elements of native ad, customize your native UI and register your
                         * NativeView
                         */
                        String description = ad.getDescription();
                        String titleForAd = ad.getAdCallToAction();
                        String titleForAdButton = ad.getAdCallToAction();
                        String title = ad.getTitle();
                        String imgUrl = ad.getCoverImageUrl();
                        ImageView nativeCoverImage = (ImageView) mNativeContainView.findViewById(R.id.native_coverImage);
                        TextView nativeTitle = (TextView) mNativeContainView.findViewById(R.id.native_title);
                        TextView nativeDescription = (TextView) mNativeContainView.findViewById(R.id.native_description);
                        TextView nativeAdButton = (TextView) mNativeContainView.findViewById(R.id.native_titleForAdButton);

                        if (!TextUtils.isEmpty(titleForAd) && !TextUtils.isEmpty(titleForAdButton)) {
                            nativeDescription.setText(titleForAd);
                            nativeAdButton.setText(titleForAdButton);
                            nativeTitle.setText(title);
                            nativeDescription.setText(description);
                            displayImg(nativeCoverImage, imgUrl);
                            if (mAdsManager != null) {
                                mAdsManager.registerNativeView(mNativeContainView);
                            }
                        }
                    }

                    @Override
                    public void onAdOpened() {
                        Toast.makeText(getActivity(), "Native Opened", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdClicked() {
                        Toast.makeText(getActivity(), "Native Clicked", Toast.LENGTH_SHORT).show();
                    }
                });
                mAdsManager.loadAd();
                break;
            default:
                break;
        }
    }

    private void displayImg(final ImageView iv, String imgUrl) {
        if (TextUtils.isEmpty(imgUrl)) {
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(this.getActivity().getApplicationContext());
        ImageLoader loader = new ImageLoader(queue, new VolleyUtil.BitmapLruCache());
        loader.get(imgUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                if (imageContainer == null) {
                    return;
                }
                Bitmap bitmap = imageContainer.getBitmap();
                if (bitmap != null) {
                    iv.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
    }

    private void setViewVisible(int mProgressvisible) {
        mLoadingLayout.setVisibility(mProgressvisible);
    }

    @Override
    public void onDestroyView() {
        mAdViewBannerContainer.removeView(mLoadAds);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mLoadAds = null;
        if (mAdsManager != null) {
            mAdsManager.destroy();
        }
        super.onDestroy();
    }

}

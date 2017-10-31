package com.pingstart.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.pingstart.adsdk.listener.NativeListener;
import com.pingstart.adsdk.model.Ad;
import com.pingstart.adsdk.utils.VolleyUtil;
import com.pingstart.utils.DataUtils;

public class LoadAdNativeFragment extends Fragment implements NativeListener, OnClickListener {
    private View mAdViewNativeContainer;
    private RelativeLayout mLoadingLayout;
    private AdManager mAdsManager;
    private Button mShowNative;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mNativeView = inflater.inflate(R.layout.fragment_native, container, false);
        mAdViewNativeContainer = mNativeView.findViewById(R.id.native_container);
        mLoadingLayout = (RelativeLayout) mNativeView.findViewById(R.id.fresh_ad_show);
        mShowNative = (Button) mNativeView.findViewById(R.id.show_native);
        mShowNative.setOnClickListener(this);
        setViewVisible(View.INVISIBLE, View.VISIBLE);
        return mNativeView;
    }

    @Override
    public void onClick(View v) {
        setViewVisible(View.VISIBLE, View.INVISIBLE);
        if (mAdsManager == null) {
            mAdsManager = new AdManager(getActivity(), DataUtils.ADS_APPID, DataUtils.ADS_SLOTID, DataUtils.ADS_PLACEMENT_ID_CPM, DataUtils.ADS_PLACEMENT_ID_FILL);
            mAdsManager.setListener(this);
            mAdsManager.loadAd();
        } else {
            mAdsManager.unregisterNativeView();
            mAdsManager.destroy();
            mAdsManager.reLoadAd();
        }
    }

    @Override
    public void onAdError(String s) {
        setViewVisible(View.INVISIBLE, View.VISIBLE);
        Toast.makeText(getActivity(), "Native Erro", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdLoaded(AdManager adManager, Ad ad) {
        if (ad == null) {
            return;
        }
        setViewVisible(View.INVISIBLE, View.INVISIBLE);
        String titleForAd = ad.getAdCallToAction();
        String titleForAdButton = ad.getAdCallToAction();
        String description = ad.getDescription();
        String title = ad.getTitle();
        String imgUrl = ad.getCoverImageUrl();
        ImageView nativeCoverImage = (ImageView) mAdViewNativeContainer.findViewById(R.id.native_coverImage);
        TextView nativeTitle = (TextView) mAdViewNativeContainer.findViewById(R.id.native_title);
        TextView nativeDescription = (TextView) mAdViewNativeContainer.findViewById(R.id.native_description);
        TextView nativeAdButton = (TextView) mAdViewNativeContainer.findViewById(R.id.native_titleForAdButton);
        TextView nativeAdflag = (TextView) mAdViewNativeContainer.findViewById(R.id.native_adflag);
        nativeAdflag.setText(getString(R.string.banner_adflag));
        if (!TextUtils.isEmpty(titleForAd) && !TextUtils.isEmpty(titleForAdButton)) {
            nativeAdButton.setText(titleForAdButton);
            nativeDescription.setText(description);
            nativeTitle.setText(title);
            displayImg(nativeCoverImage, imgUrl);
            mAdViewNativeContainer.setVisibility(View.VISIBLE);
            if (mAdsManager != null) {
                mAdsManager.registerNativeView(mAdViewNativeContainer);
            }
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

    @Override
    public void onAdOpened() {
        Toast.makeText(getActivity(), "Native Opened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdClicked() {
        Toast.makeText(getActivity(), "Native Clicked", Toast.LENGTH_SHORT).show();
    }

    private void isParentNull() {
        if (mAdViewNativeContainer.getParent() != null) {
            ViewGroup parent = (ViewGroup) mAdViewNativeContainer.getParent();
            if (parent != null) {
                parent.removeView(mAdViewNativeContainer);
            }
        }
    }

    @Override
    public void onDestroyView() {
        isParentNull();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (mAdsManager != null) {
            mAdsManager.destroy();
        }
        super.onDestroy();
    }

    private void setViewVisible(int mProgressvisible, int mButtonvisible) {
        mLoadingLayout.setVisibility(mProgressvisible);
        mShowNative.setVisibility(mButtonvisible);
    }
}
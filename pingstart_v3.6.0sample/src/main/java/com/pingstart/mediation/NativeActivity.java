package com.pingstart.mediation;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.ads.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.pingstart.adsdk.listener.NativeListener;
import com.pingstart.adsdk.mediation.PingStartNative;
import com.pingstart.adsdk.model.BaseNativeAd;
import com.pingstart.mobileads.AdMobAdvanceNativeAd;
import com.pingstart.mobileads.FacebookNativeAd;

import java.util.List;

/**
 * Created by base on 2016/6/1.
 */
public class NativeActivity extends Activity {

    private static final String TAG = NativeActivity.class.getSimpleName();

    private TextView titleTextView, contentTextView;
    private Button mActionButton;
    private PingStartNative pingStartNative;
    private ImageView adImageView;
    private ViewGroup lytNative;
    private NativeContentAdView lytAdMobContent;
    private NativeAppInstallAdView lytAdMobInstall;
    private ViewGroup lytPsNative;
    private MediaView mFbNative;
    private ImageView mAdImageView;
    private TextView mTitleTextView, mContentTextView;

    private PingStartNative mNativeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_native);

        adImageView = (ImageView) findViewById(R.id.ad_image);
        titleTextView = (TextView) findViewById(R.id.title);
        contentTextView = (TextView) findViewById(R.id.content);
        mActionButton = (Button) findViewById(R.id.ad_btn);
        lytNative = (ViewGroup) findViewById(R.id.test);
        lytNative.setVisibility(View.GONE);
        lytAdMobContent = (NativeContentAdView) lytNative.findViewById(R.id.lyt_admob_content);
        lytAdMobContent.setVisibility(View.GONE);
        lytAdMobInstall = (NativeAppInstallAdView) lytNative.findViewById(R.id.lyt_admob_install);
        lytAdMobInstall.setVisibility(View.GONE);
        lytPsNative = (ViewGroup) lytNative.findViewById(R.id.ps_native);
        lytPsNative.setVisibility(View.GONE);

        mFbNative = new MediaView(this);
        ViewGroup main = (ViewGroup) findViewById(R.id.lyt_main);
        mFbNative.setVisibility(View.GONE);
        main.addView(mFbNative);

        new Thread(new Runnable() {
            @Override
            public void run() {
                titleTextView.setText("");
                contentTextView.setText("");
                mActionButton.setText("");
                adImageView.setImageBitmap(null);
                int xxxx = getIntent().getIntExtra("xxxx", 1000223);
                pingStartNative = new PingStartNative(NativeActivity.this, String.valueOf(xxxx));
                //pingStartNative = new PingStartNative(NativeActivity.this, "1000223");
                pingStartNative.setAdListener(new NativeListener() {
                    @Override
                    public void onAdLoaded(BaseNativeAd ad) {
                        Log.d(TAG, "onAdLoaded native");
                        if (ad != null) {
                            String workName = ad.getNetworkName();
                            if (workName != null && workName.equalsIgnoreCase("facebook")) {
                                try {
                                    mFbNative.setVisibility(View.VISIBLE);
                                    lytNative.setVisibility(View.GONE);
                                    mFbNative.setAutoplay(true);
                                    FacebookNativeAd nativeAd = (FacebookNativeAd) ad;
                                    mFbNative.setNativeAd(nativeAd.getNativeAd());
                                    pingStartNative.registerNativeView(mFbNative);
                                } catch (Exception e) {
//                                    ExceptionHandlerFactory.createExceptionHandler().handleException(e);
                                }
                            } else if (workName != null && workName.equalsIgnoreCase("admob")) {

                                AdMobAdvanceNativeAd adMobAdvanceNativeAd = (AdMobAdvanceNativeAd) ad;
                                lytNative.setVisibility(View.VISIBLE);
                                mFbNative.setVisibility(View.GONE);
                                lytPsNative.setVisibility(View.GONE);
                                if (adMobAdvanceNativeAd.getAdType().equalsIgnoreCase("content")) {

                                    lytAdMobContent.setVisibility(View.VISIBLE);
                                    lytAdMobInstall.setVisibility(View.GONE);
                                    populateContentAdView(adMobAdvanceNativeAd.getNativeContentAd(), lytAdMobContent);
                                } else if (((AdMobAdvanceNativeAd) ad).getAdType().equalsIgnoreCase("install")) {
                                    lytAdMobContent.setVisibility(View.GONE);
                                    lytAdMobInstall.setVisibility(View.VISIBLE);
                                    populateAppInstallAdView(adMobAdvanceNativeAd.getNativeInstallAd(), lytAdMobInstall);
                                }
                            } else {
                                mFbNative.setVisibility(View.GONE);
                                lytNative.setVisibility(View.VISIBLE);
                                lytAdMobContent.setVisibility(View.GONE);
                                lytAdMobInstall.setVisibility(View.GONE);
                                lytPsNative.setVisibility(View.VISIBLE);
                                titleTextView.setText(ad.getTitle());
                                contentTextView.setText(ad.getDescription());
                                mActionButton.setText(ad.getAdCallToAction());
                                ad.displayCoverImage(adImageView);
                                pingStartNative.registerNativeView(mActionButton);
                            }
                        }
                    }

                    @Override
                    public void onAdError(String error) {
                        Log.i("Lebron", "native onAdError :" + error);
                        lytNative.setVisibility(View.GONE);
                        pingStartNative.destroy();
                    }

                    @Override
                    public void onAdClicked() {
                        Log.i("Lebron", "native onAdClicked");
                    }
                });
                pingStartNative.loadAd();
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pingStartNative != null) {
            pingStartNative.destroy();
            pingStartNative = null;
        }
    }

    private void populateContentAdView(NativeContentAd nativeContentAd,
                                       NativeContentAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.contentad_headline));
        adView.setImageView(adView.findViewById(R.id.contentad_image));
        adView.setBodyView(adView.findViewById(R.id.contentad_body));
        adView.setCallToActionView(adView.findViewById(R.id.contentad_call_to_action));
        adView.setLogoView(adView.findViewById(R.id.contentad_logo));
        adView.setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));
        // Some assets are guaranteed to be in every NativeContentAd.
        ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(nativeContentAd.getCallToAction());
        ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());

        List<NativeAd.Image> images = nativeContentAd.getImages();

        if (images.size() > 0) {
            ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }

        // Some aren't guaranteed, however, and should be checked.
        NativeAd.Image logoImage = nativeContentAd.getLogo();

        if (logoImage == null) {
            adView.getLogoView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
            adView.getLogoView().setVisibility(View.VISIBLE);
        }
        pingStartNative.registerNativeView(adView.getCallToActionView());
        // Assign native ad object to the native view.
//        adView.setNativeAd(nativeContentAd);
    }

    private void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd,
                                          NativeAppInstallAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        adView.setImageView(adView.findViewById(R.id.appinstall_image));
        adView.setBodyView(adView.findViewById(R.id.appinstall_body));
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
        adView.setPriceView(adView.findViewById(R.id.appinstall_price));
        adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));
        adView.setStoreView(adView.findViewById(R.id.appinstall_store));

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
        ((ImageView) adView.getIconView()).setImageDrawable(nativeAppInstallAd.getIcon()
                .getDrawable());

        List<NativeAd.Image> images = nativeAppInstallAd.getImages();

        if (images.size() > 0) {
            ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }

        // Some aren't guaranteed, however, and should be checked.
        if (nativeAppInstallAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAppInstallAd.getPrice());
        }

        if (nativeAppInstallAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAppInstallAd.getStore());
        }

        if (nativeAppInstallAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAppInstallAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
//        adView.setNativeAd(nativeAppInstallAd);
        pingStartNative.registerNativeView(adView.getCallToActionView());
    }
}

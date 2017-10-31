package com.pingstart.mediation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.ads.MediaView;
import com.pingstart.adsdk.mediation.PingStartMultiple;
import com.pingstart.adsdk.model.BaseNativeAd;
import com.pingstart.mediation.R;
import com.pingstart.mobileads.FacebookNativeAd;

import java.util.List;

/**
 * Created by haipingguo on 16-12-26.
 */
public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.AdViewHolder> {
    private Context mContext;
    private List<BaseNativeAd> mAds;
    private PingStartMultiple mNativeAdsManager;

    public AdsAdapter(Context context, List<BaseNativeAd> ads, PingStartMultiple nativeAdsManager) {
        this.mContext = context;
        this.mAds = ads;
        this.mNativeAdsManager = nativeAdsManager;
    }

    @Override
    public AdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AdViewHolder holder = new AdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.multi_native_ad_layout, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(AdViewHolder holder, int position) {
        BaseNativeAd ad = mAds.get(position);
        if (ad != null) {
            if (ad.getNetworkName().equalsIgnoreCase("facebook")) {
                holder.imageView.setVisibility(View.GONE);
                holder.mediaView.setVisibility(View.VISIBLE);
                FacebookNativeAd nativeAd = (FacebookNativeAd) ad;
                holder.mediaView.setNativeAd(nativeAd.getNativeAd());
            } else {
                holder.imageView.setVisibility(View.VISIBLE);
                holder.mediaView.setVisibility(View.GONE);
                ad.displayCoverImage(holder.imageView);
            }
            holder.titleView.setText(ad.getTitle());
            holder.contentView.setText(ad.getDescription());
            holder.actionView.setText(ad.getAdCallToAction());
            if (mNativeAdsManager != null) {
                mNativeAdsManager.registerNativeView(ad, holder.actionView);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mAds.size();
    }

    class AdViewHolder extends RecyclerView.ViewHolder {

        TextView titleView;
        TextView contentView;
        TextView actionView;
        ImageView imageView;
        MediaView mediaView;

        public AdViewHolder(View view) {
            super(view);
            titleView = (TextView) view.findViewById(R.id.native_title);
            contentView = (TextView) view.findViewById(R.id.native_description);
            actionView = (TextView) view.findViewById(R.id.native_titleForAdButton);
            imageView = (ImageView) view.findViewById(R.id.native_coverImage);
            mediaView = (MediaView) view.findViewById(R.id.fb_native);
        }
    }
}

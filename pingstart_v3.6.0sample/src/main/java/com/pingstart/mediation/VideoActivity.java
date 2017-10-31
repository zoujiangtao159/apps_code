package com.pingstart.mediation;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pingstart.adsdk.listener.RewardVideoListener;
import com.pingstart.adsdk.mediation.PingStartVideo;
import com.pingstart.adsdk.model.PingStartReward;

public class VideoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        int xxxx = getIntent().getIntExtra("xxxx", 4);
        PingStartVideo.loadRewardedVideo(String.valueOf(xxxx));

        final Button show = (Button) findViewById(R.id.btn_video);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PingStartVideo.hasRewardedVideo()) {
                    PingStartVideo.showRewardedVideo();
                } else {
                    Toast.makeText(VideoActivity.this, "视频准备中", Toast.LENGTH_SHORT).show();
                }
            }
        });
        PingStartVideo.setRewardedVideoListener(new RewardVideoListener() {

            @Override
            public void onVideoAdClosed() {
                finish();
            }

            @Override
            public void onVideoStarted() {
                show.setEnabled(false);
            }

            @Override
            public void onVideoLoaded() {
                show.setEnabled(true);
            }

            @Override
            public void onVideoCompleted(PingStartReward pingStartReward) {
                Toast.makeText(VideoActivity.this,
                        "视频播放完成，获得奖励" + pingStartReward.getLabel() + "+数量 ： " + pingStartReward.getAmount(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdError(String s) {
                Log.d("messi", "loadAdError: " + s);
                Toast.makeText(VideoActivity.this, "视频加载错误" + s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked() {
                Toast.makeText(VideoActivity.this, "广告被点击", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PingStartVideo.destroy();
    }
}

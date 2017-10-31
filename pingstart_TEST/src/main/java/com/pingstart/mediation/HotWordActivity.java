package com.pingstart.mediation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
//import com.pingstart.adsdk.listener.SearchAdsListener;
//import com.pingstart.adsdk.mediation.PingStartSearch;
//import com.pingstart.adsdk.model.SearchAds;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HotWordActivity extends Activity/* implements AdapterView.OnItemClickListener, View.OnClickListener*/ {

//    private static final String TAG = HotWordActivity.class.getSimpleName();
//
//    private static final int RANDOM_SIZE = 9;
//
//    private GridView mLytHotWord;
//    private Button mBtnSearch;
//    private EditText mEdtSearch;
//
//   // private List<SearchAds> mHotWords;
//   // private List<Integer> mIndex = new ArrayList<>();
//   // private PingStartSearch mPingStartWord;
//    private boolean hasImage = true;
//
//
//    private int[] mBackgroundColors = new int[]{
//            Color.LTGRAY, Color.BLUE,
//            Color.GRAY, Color.MAGENTA,
//            Color.RED, Color.YELLOW,
//            Color.CYAN, Color.DKGRAY,
//            Color.GREEN
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_hot_word);
//        loadHotWord();
//    }
//
//    @Override
//    public void onContentChanged() {
//        mLytHotWord = (GridView) findViewById(R.id.lyt_hotword);
//        mLytHotWord.setOnItemClickListener(this);
//
//        mBtnSearch = (Button) findViewById(R.id.btn_search);
//        mBtnSearch.setOnClickListener(this);
//        mEdtSearch = (EditText) findViewById(R.id.edt_search);
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Log.d(TAG, "click position:" + position + " index = " + mIndex.get(position));
////        mPingStartWord.onSearchAdsClick(this, mSearchAdses.get(mIndex.get(position)));
//    }
//
//    @Override
//    /*public void onClick(View v) {
//        if (v == mBtnSearch) {
//            if (mPingStartWord != null) {
//                String keyword = mEdtSearch.getText().toString();
//                mPingStartWord.searchCustomKeyword(this, keyword);
//            }
//        }
//    }*/
//
//    private void loadHotWord() {
//        mPingStartWord = new PingStartSearch(this, "1000596");
//        mPingStartWord.setAdListener(new SearchAdsListener() {
//            @Override
//            public void onAdFailed(String error) {
//                Log.e("Search", "load hot word error:" + error);
//            }
//
//            @Override
//            public void onAdLoaded(List<SearchAds> hotWords) {
//
//                mHotWords = hotWords;
//                if (mIndex != null) {
//                    mIndex.clear();
//                }
//                generateRandom();
//                hasImage();
//                mLytHotWord.setAdapter(new HotWordAdapter());
//            }
//
//            @Override
//            public void onAdClicked() {
//
//            }
//        });
//        mPingStartWord.loadAd();
//    }
//
//    private void generateRandom() {
//        Random random = new Random();
//        for (int i = 0; i < RANDOM_SIZE; i++) {
//            int index = random.nextInt(mHotWords.size());
//            Log.d("Search", "random index is :" + index);
//            mIndex.add(index);
//        }
//    }
//
//    private void hasImage() {
//        boolean[] flags = new boolean[RANDOM_SIZE];
//
//        for (int i = 0; i < RANDOM_SIZE; i++) {
//            SearchAds hotWord = mHotWords.get(mIndex.get(i));
//            if (TextUtils.isEmpty(hotWord.getUrlImage())) {
//                flags[i] = false;
//            } else {
//                flags[i] = true;
//            }
//        }
//
//        for (boolean flag : flags) {
//            Log.d("Search", "flags :" + flag);
//            if (!flag) {
//                hasImage = false;
//            }
//        }
//    }
//
//    private class HotWordAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return 9;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return mHotWords.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            final ViewHolder holder;
//            if (convertView == null) {
//                LayoutInflater inflater = LayoutInflater.from(HotWordActivity.this);
//                if (hasImage) {
//                    convertView = inflater.inflate(R.layout.item_hot_word_with_image, parent, false);
//                } else {
//                    convertView = inflater.inflate(R.layout.item_hot_word_without_image, parent, false);
//                }
//                holder = new ViewHolder(convertView);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//
//            SearchAds word = mHotWords.get(mIndex.get(position));
//            if (hasImage) {
//                String imgUrl = word.getUrlImage();
//                RequestQueue queue = Volley.newRequestQueue(HotWordActivity.this);
//                ImageRequest imageRequest = new ImageRequest(imgUrl, new Response.Listener<Bitmap>() {
//
//                    @Override
//                    public void onResponse(Bitmap bitmap) {
//                        holder.imageView.setImageBitmap(bitmap);
//                    }
//                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        Log.e("Search", "load hot image error:" + volleyError.toString());
//                    }
//                });
//                queue.add(imageRequest);
//                holder.txtWithImage.setText(word.getTitle());
//            } else {
//                Random random = new Random();
//                holder.txtWithoutImage.setBackgroundColor(mBackgroundColors[random.nextInt(RANDOM_SIZE)]);
//                holder.txtWithoutImage.setText(word.getTitle());
//            }
//            //注册点击
//            mPingStartWord.registerSearchAdsClickListener(HotWordActivity.this, convertView, word);
//            //注册展示
//            mPingStartWord.registerSearchAdsImpression(word);
//            return convertView;
//        }
//
//        class ViewHolder {
//            ImageView imageView;
//            TextView txtWithImage;
//            TextView txtWithoutImage;
//
//            public ViewHolder(View view) {
//                if (hasImage) {
//                    imageView = (ImageView) view.findViewById(R.id.img_hotword);
//                    txtWithImage = (TextView) view.findViewById(R.id.txt_hotword);
//                } else {
//                    txtWithoutImage = (TextView) view.findViewById(R.id.txt_hotword_title);
//                }
//            }
//        }
//    }
}

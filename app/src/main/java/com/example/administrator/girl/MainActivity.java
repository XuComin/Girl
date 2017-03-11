package com.example.administrator.girl;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener {
    private static final String TAG = "MainActivity";
    @InjectView(R.id.lv)
    ListView mLv;
    private List<GirlBean.ResultsBean> mList = new ArrayList<>();
    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        // Log.d(TAG, "onCreate: ...............................");

        initData();
        setListener();

    }

    private void setListener() {
        mLv.setOnScrollListener(MainActivity.this);
    }


    private void initData() {
        getImage();
        mAdapter = new Adapter();
        mLv.setAdapter(mAdapter);
    }

    private void getImage() {
        //System.out.println("请求");
        OkHttpClient client = new OkHttpClient();
        String url = "http://gank.io/api/data/福利/10/1";
        Request request = new Request.Builder().get().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: 请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                //Log.d(TAG, "onResponse: "+str);
                Gson gson = new Gson();
                GirlBean girlBean = gson.fromJson(str, GirlBean.class);
                List<GirlBean.ResultsBean> results = girlBean.getResults();
                mList.addAll(results);
                // Log.d(TAG, "onResponse: " + mList.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();

                    }
                });
                SystemClock.sleep(1000);

            }
        });


    }

    private void loadMoreData() {
        isLoading = true;
        OkHttpClient client = new OkHttpClient();
        Log.d(TAG, "loadMoreData:....." + mList.size());
        String url = "http://gank.io/api/data/福利/10/" + mList.size() / 10 + 1;
        Request request = new Request.Builder().get().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: 请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                //Log.d(TAG, "onResponse: "+str);
                Gson gson = new Gson();
                GirlBean girlBean = gson.fromJson(str, GirlBean.class);
                List<GirlBean.ResultsBean> results = girlBean.getResults();
                mList.addAll(results);
                // Log.d(TAG, "onResponse: " + mList.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        isLoading = false;
                    }
                });
                // SystemClock.sleep(1000);

            }
        });

    }

    private boolean isLoading;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            if (mLv.getLastVisiblePosition() == mList.size() - 1 && !isLoading) {
                loadMoreData();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            System.out.println("getCount: " + mList.size());
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.item_girl, null);
                holder = new ViewHolder();
                holder.iv = (ImageView) convertView.findViewById(R.id.iv_image);
                holder.tv = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            GirlBean.ResultsBean resultsBean = mList.get(position);
            String url = resultsBean.getUrl();
            holder.tv.setText(resultsBean.getPublishedAt());
            // Picasso.with(MainActivity.this).load(url).resize(400,600).centerInside().into(holder.iv);
            Glide.with(MainActivity.this).load(url).centerCrop().bitmapTransform(new CropCircleTransformation(MainActivity.this)).into(holder.iv);
            return convertView;
        }
    }

    class ViewHolder {
        ImageView iv;
        TextView tv;
    }

}

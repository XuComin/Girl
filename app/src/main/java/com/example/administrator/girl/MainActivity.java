package com.example.administrator.girl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
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
        getImage();
        initData();

    }

    private void initData() {
        //System.out.println(mLv + "mlistview");
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
                Log.d(TAG, "onResponse: " + mList.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();

                    }
                });

            }
        });


    }

    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
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
            Picasso.with(MainActivity.this).load(url).resize(400,600).centerInside().into(holder.iv);
            return convertView;
        }
    }
    class ViewHolder {
        ImageView iv;
        TextView tv;
    }

}

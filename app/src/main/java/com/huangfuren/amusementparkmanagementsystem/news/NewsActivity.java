package com.huangfuren.amusementparkmanagementsystem.news;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.huangfuren.amusementparkmanagementsystem.R;
import com.huangfuren.amusementparkmanagementsystem.model.News;
import com.huangfuren.amusementparkmanagementsystem.adapter.NewsAdapter;
import com.socks.okhttp.plus.OkHttpProxy;
import com.socks.okhttp.plus.callback.OkCallback;
import com.socks.okhttp.plus.parser.OkJsonParser;


import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;

import static com.huangfuren.amusementparkmanagementsystem.MainActivity.ipAddress;

public class NewsActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private List<News> newsList = new ArrayList<>();
    private NewsAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private Context mContext;
    private int role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        mContext = this;
        role = PreferenceManager.getDefaultSharedPreferences(mContext).getInt("role",0);
        initNews();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("新闻啦");
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(adapter);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFruits();
            }
        });
    }
    private void refreshFruits() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initNews();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void initNews() {
        newsList.clear();

        OkHttpClient okHttpClient = OkHttpProxy.getInstance();
        OkHttpProxy.get()
                .url("http://"+ipAddress+"/APMS/news")
                .tag(this)
                .enqueue(new OkCallback<List<News>>(new OkJsonParser<List<News>>() {
                }) {
                    @Override
                    public void onSuccess(int code, List<News> newsList) {
                        NewsActivity.this.newsList.addAll(newsList);
                        adapter.notifyDataSetChanged();
                        //Log.e("xxxxxx",newsList.toString());
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        //Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Toasty.error(mContext,"网络异常").show();
                        Log.e("网络异常",e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.news_setting:
                Intent intent = new Intent(NewsActivity.this, IssueNewActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (role == 2) {
            getMenuInflater().inflate(R.menu.news, menu);
            return true;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initNews();
        adapter.notifyDataSetChanged();
    }
}

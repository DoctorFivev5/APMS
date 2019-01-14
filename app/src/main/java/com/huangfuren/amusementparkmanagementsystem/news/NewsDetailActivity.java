package com.huangfuren.amusementparkmanagementsystem.news;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.huangfuren.amusementparkmanagementsystem.R;
import com.huangfuren.amusementparkmanagementsystem.model.News;

import static com.huangfuren.amusementparkmanagementsystem.MainActivity.ipAddress;

public class NewsDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView imageView;
    private TextView newsBody;
    private TextView newsMaker;
    private ActionBar actionBar;
    private News news;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        initView();
    }

    private void initView() {
        news = (News) getIntent().getExtras().getSerializable("news");
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        imageView = findViewById(R.id.ic_image);
        newsBody = findViewById(R.id.news_text);
        newsMaker = findViewById(R.id.news_maker);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(news.getTitle());
        Log.e("hhh","http://"+ipAddress+news.getImageUrl());
        Glide.with(this).load("http://"+ipAddress+news.getImageUrl()).into(imageView);
        newsBody.setText(news.getBody());
        newsMaker.setText("by "+news.getUser_id());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

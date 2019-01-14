package com.huangfuren.amusementparkmanagementsystem.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huangfuren.amusementparkmanagementsystem.R;
import com.huangfuren.amusementparkmanagementsystem.model.News;
import com.huangfuren.amusementparkmanagementsystem.news.NewsDetailActivity;

import java.util.List;

import static com.huangfuren.amusementparkmanagementsystem.MainActivity.ipAddress;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>{

    private static final String TAG = "NewsAdapter";

    private Context mContext;

    private List<News> mNewsList;

    public void setmNewsList(List<News> mNewsList) {
        this.mNewsList = mNewsList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView newsImage;
        TextView newsName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            newsImage = (ImageView) view.findViewById(R.id.news_image);
            newsName = (TextView) view.findViewById(R.id.news_name);
        }
    }

    public NewsAdapter(List<News> newstList) {
        mNewsList = newstList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.news_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                News news = mNewsList.get(position);
                Intent intent = new Intent(mContext, NewsDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("news", news);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        News news = mNewsList.get(position);
        holder.newsName.setText(news.getTitle());
        Log.e("aaa","http://"+ipAddress+news.getImageUrl());
        Glide.with(mContext).load("http://"+ipAddress+news.getImageUrl()).into(holder.newsImage);
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

}

package com.huangfuren.amusementparkmanagementsystem.adapter;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.huangfuren.amusementparkmanagementsystem.R;
import com.huangfuren.amusementparkmanagementsystem.fix.FixActivity;
import com.huangfuren.amusementparkmanagementsystem.map.MapActivity;
import com.huangfuren.amusementparkmanagementsystem.map.MapDemoActivity;
import com.huangfuren.amusementparkmanagementsystem.model.Main;
import com.huangfuren.amusementparkmanagementsystem.news.NewsActivity;
import com.huangfuren.amusementparkmanagementsystem.queue.QueueMainActivity;
import com.huangfuren.amusementparkmanagementsystem.queue.QueueMainActivity2;
import com.huangfuren.amusementparkmanagementsystem.ticket.TicketActivity;

import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by DoctorFive on 2018/12/17.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private static final String TAG = "MainAdapter";

    private Context mContext;

    private List<Main> mMainList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView newsImage;
        TextView newsName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            newsImage = (ImageView) view.findViewById(R.id.main_image);
            newsName = (TextView) view.findViewById(R.id.main_name);
        }
    }

    public MainAdapter(List<Main> mainList) {
        mMainList = mainList;
    }

    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_item, parent, false);
        final MainAdapter.ViewHolder holder = new MainAdapter.ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Main main = mMainList.get(position);
                int role = PreferenceManager.getDefaultSharedPreferences(mContext).getInt("role",0);
                switch (main.getTitle()){
                    case "新闻活动":
                        if (role == 0){
                            Toasty.info(mContext, "请登录").show();
                        }else if (role == 1) {
                            Intent intentNews = new Intent(mContext, NewsActivity.class);
                            mContext.startActivity(intentNews);
                        }
                        else if (role == 2){
                            Intent intentNews = new Intent(mContext, NewsActivity.class);
                            mContext.startActivity(intentNews);
                        }
                        break;

                    case "购票":
                        Intent intentTicket = new Intent(mContext, TicketActivity.class);
                        mContext.startActivity(intentTicket);
                        break;

                    case "维修":
                        Intent intentFix = new Intent(mContext, FixActivity.class);
                        mContext.startActivity(intentFix);
                        break;

                    case "地图":
                        Intent intentMap = new Intent(mContext, MapActivity.class);
                        mContext.startActivity(intentMap);
                        break;

                    case "排队":
                        if (role == 0){
                            Toasty.info(mContext, "请登录").show();
                        }else if (role == 1) {
                            //游客排队界面
                            Intent intentQueue = new Intent(mContext, QueueMainActivity.class);
                            mContext.startActivity(intentQueue);
                        }
                        else if (role == 2){
                            //管理员排队界面
                            Intent intentQueue = new Intent(mContext, QueueMainActivity2.class);
                            mContext.startActivity(intentQueue);
                        }
                        break;
                }

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(MainAdapter.ViewHolder holder, int position) {
        Main main = mMainList.get(position);
        holder.newsName.setText(main.getTitle());
        RequestOptions options = new RequestOptions()
                .override(500, 500);
        Glide.with(mContext).load(main.getImageId()).apply(options).into(holder.newsImage);
    }

    @Override
    public int getItemCount() {
        return mMainList.size();
    }

}

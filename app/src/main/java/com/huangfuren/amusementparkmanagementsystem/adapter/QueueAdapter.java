package com.huangfuren.amusementparkmanagementsystem.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.huangfuren.amusementparkmanagementsystem.R;
import com.huangfuren.amusementparkmanagementsystem.model.Queue;
import com.huangfuren.amusementparkmanagementsystem.model.QueueItem;
import com.huangfuren.amusementparkmanagementsystem.queue.QueueMainActivity;
import com.socks.okhttp.plus.OkHttpProxy;
import com.socks.okhttp.plus.callback.OkCallback;
import com.socks.okhttp.plus.parser.OkTextParser;

import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;

import static com.huangfuren.amusementparkmanagementsystem.MainActivity.ipAddress;

/**
 * Created by miracle on 2019/1/1.
 */

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {
    private static final String TAG = "QueueAdapter";

    private Context mContext;

    private List<QueueItem> mQueueList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView projectName, queue_number, queue_time;
        LinearLayout item;
        public ViewHolder(View view) {
            super(view);
            item = (LinearLayout)view;
            projectName = view.findViewById(R.id.project_name);
            queue_number = view.findViewById(R.id.queue_number);
            queue_time = view.findViewById(R.id.queue_time);
        }
    }

    public QueueAdapter(List<QueueItem> queueList) {
        mQueueList = queueList;
    }

    @Override
    public QueueAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.queue_item, parent, false);
        final QueueAdapter.ViewHolder holder = new QueueAdapter.ViewHolder(view);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(mContext);

                dialogBuilder
                        .withTitle("取消排队吗")                                  //.withTitle(null)  no title
                        .withTitleColor("#FFFFFF")                                  //def
                        .withDividerColor("#11000000")                              //def
                        .withMessage(null)                     //.withMessage(null)  no Msg
                        .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                        .withDialogColor("#FF87ceeb")                               //def  | withDialogColor(int resid)
                        .withDuration(700)                                          //def
                        .withButton1Text("确定")                                      //def gone
                        .withButton2Text("关闭")                                  //def gone
                        .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelQueue(holder.getAdapterPosition(),dialogBuilder);
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogBuilder.dismiss();
                            }
                        })
                        .show();


            }
        });
        return holder;
    }

    private void cancelQueue(final int position, final Dialog dialog) {
        Queue queue = new Queue();
        //获取list中对应项的队列id
        queue.setId(mQueueList.get(position).getQueueId());
        queue.setProject_id(mQueueList.get(position).getProjectId());
        queue.setNumber(mQueueList.get(position).getNumber());
        OkHttpClient okHttpClient = OkHttpProxy.getInstance();
        OkHttpProxy.post()
                .url("http://"+ipAddress+"/APMS/deleteQueue")
                .tag(this)
                .addParams("queue", JSON.toJSONString(queue))
                .enqueue(new OkCallback<String>(new OkTextParser() {
                }) {
                    @Override
                    public void onSuccess(int code,String text) {
                        if (text.equals("1")){
                            mQueueList.remove(position);
                            QueueAdapter.this.notifyDataSetChanged();
                            Toasty.info(mContext,"取消排队").show();
                            ((QueueMainActivity) mContext).getQueueList();
                            dialog.dismiss();
                        }
                        else {
                            Toasty.error(mContext, "取消排队失败").show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e("网络异常",e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void onBindViewHolder(QueueAdapter.ViewHolder holder, int position) {
        QueueItem queueItem = mQueueList.get(position);
        holder.projectName.setText(queueItem.getProjectName());
        holder.queue_number.setText("当前位置：第"+String.valueOf(queueItem.getNumber()));
        holder.queue_time.setText("  预计等待时间："+(queueItem.getNumber()-1)*5+"分钟");
    }

    @Override
    public int getItemCount() {
        return mQueueList.size();
    }

}

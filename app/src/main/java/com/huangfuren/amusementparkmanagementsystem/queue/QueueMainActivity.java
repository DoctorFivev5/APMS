package com.huangfuren.amusementparkmanagementsystem.queue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import com.huangfuren.amusementparkmanagementsystem.R;
import com.huangfuren.amusementparkmanagementsystem.adapter.QueueAdapter;
import com.huangfuren.amusementparkmanagementsystem.model.Project;
import com.huangfuren.amusementparkmanagementsystem.model.Queue;
import com.huangfuren.amusementparkmanagementsystem.model.QueueItem;
import com.huangfuren.amusementparkmanagementsystem.model.User;
import com.huangfuren.amusementparkmanagementsystem.ticket.TicketActivity;
import com.socks.okhttp.plus.OkHttpProxy;
import com.socks.okhttp.plus.callback.OkCallback;
import com.socks.okhttp.plus.parser.OkJsonParser;
import com.socks.okhttp.plus.parser.OkTextParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;

import static com.huangfuren.amusementparkmanagementsystem.MainActivity.ipAddress;

public class QueueMainActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences sp;
    private User user;
    private Toolbar toolbar;
    private Button button;
    private RecyclerView recyclerView;
    private List<QueueItem> queueItems;
    private QueueAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_main);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        user = new User();
        user.setId(sp.getLong("user_id",0));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("排队啦");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        button = findViewById(R.id.button_native);
        recyclerView = findViewById(R.id.queue_recycler);
        button.setOnClickListener(this);
        queueItems = new ArrayList<>();
        getQueueList();
        adapter = new QueueAdapter(queueItems);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getQueueList();
            }
        });
    }

    public void getQueueList() {
        if (queueItems != null)
            queueItems.clear();
        OkHttpClient okHttpClient = OkHttpProxy.getInstance();
        OkHttpProxy.post()
                .url("http://"+ipAddress+"/APMS/searchQueue")
                .tag(this)
                .addParams("user", JSON.toJSONString(user))
                .enqueue(new OkCallback<List<QueueItem>>(new OkJsonParser<List<QueueItem>>() {
                }) {
                    @Override
                    public void onSuccess(int code, List<QueueItem> queues) {
                        //加载自己的所有排队信息
                        queueItems.addAll(queues);
//                        adapter = new QueueAdapter(queueItems);
//                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(QueueMainActivity.this);
//                        recyclerView.setLayoutManager(layoutManager);
//                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        if (swipeRefresh != null)
                            swipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Toasty.error(QueueMainActivity.this, "网络异常").show();
                        Log.e("网络异常",e.getMessage());
                        e.printStackTrace();
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_native:
                new IntentIntegrator(this)
                        .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                        //.setPrompt("请对准二维码")// 设置提示语
                        .setCameraId(0)// 选择摄像头,可使用前置或者后置
                        .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                        .setCaptureActivity(QrCodeActivity.class)//自定义扫码界面
                        .initiateScan();// 初始化扫码
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫码结果
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                //扫码失败
            } else {
                String result = intentResult.getContents();//返回值
                JSONObject jsonObject = JSONObject.parseObject(result);
                Project project = jsonObject.toJavaObject(Project.class);
                Log.e("project", project.toString());
                Queue queue = new Queue();
                queue.setUser_id(sp.getLong("user_id",0));
                queue.setProject_id(project.getId());
                queue.setNumber(0);
                OkHttpClient okHttpClient = OkHttpProxy.getInstance();
                OkHttpProxy.post()
                        .url("http://"+ipAddress+"/APMS/addQueue")
                        .tag(this)
                        .addParams("queue", JSON.toJSONString(queue))
                        .enqueue(new OkCallback<String>(new OkTextParser()) {
                            @Override
                            public void onSuccess(int code, String s) {
                                if (s.equals("1")){
                                    Toasty.success(QueueMainActivity.this,"排队成功！").show();
                                    getQueueList();
                                }else {
                                    Toasty.error(QueueMainActivity.this,"该项目已排队！").show();
                                }
                            }

                            @Override
                            public void onFailure(Throwable e) {
                                Toasty.error(QueueMainActivity.this, "网络异常").show();
                                Log.e("网络异常",e.getMessage());
                                e.printStackTrace();
                            }
                        });
            }
        }
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

package com.huangfuren.amusementparkmanagementsystem.news;

import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.alibaba.fastjson.JSON;
import com.hjq.bar.TitleBar;
import com.huangfuren.amusementparkmanagementsystem.R;
import com.huangfuren.amusementparkmanagementsystem.model.News;
import com.huangfuren.amusementparkmanagementsystem.queue.QueueMainActivity2;
import com.huangfuren.amusementparkmanagementsystem.user.RegisterActivity;
import com.socks.okhttp.plus.OkHttpProxy;
import com.socks.okhttp.plus.callback.OkCallback;
import com.socks.okhttp.plus.parser.OkJsonParser;
import com.socks.okhttp.plus.parser.OkTextParser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;

import static com.huangfuren.amusementparkmanagementsystem.MainActivity.ipAddress;

public class IssueNewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView imageView;
    private EditText titleEdit;
    private Spinner typeSpinner;
    private EditText bodyEdit;
    private Button issueBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_new);
        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("发布新闻啦");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        imageView = findViewById(R.id.news_image_new);
        titleEdit = findViewById(R.id.news_title_new);
        typeSpinner = findViewById(R.id.news_type_new);
        bodyEdit = findViewById(R.id.news_body_new);
        issueBtn = findViewById(R.id.issue);
        issueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEdit.getText().toString();
                String type = typeSpinner.getSelectedItem().toString();
                String body = bodyEdit.getText().toString();
                if (TextUtils.isEmpty(title)||TextUtils.isEmpty(type)||TextUtils.isEmpty(body)) {
                    Toasty.info(IssueNewActivity.this,"请输入内容").show();
                    return;
                }
                News news = new News();
                news.setTitle(title);
                if (type.equals("新闻"))
                    news.setType(1);
                else
                    news.setType(2);
                news.setBody(body);
                DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    String date = format1.parse(new Date().toString()).toString();
                    Log.e("date--------------",date);
                    news.setStart_Time(format1.parse(new Date().toString()).toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //news.setStart_Time(new Date().toString());
                news.setUser_id(PreferenceManager.getDefaultSharedPreferences(IssueNewActivity.this).getLong("user_id",0));
                OkHttpClient okHttpClient = OkHttpProxy.getInstance();
                OkHttpProxy.post()
                        .url("http://"+ipAddress+"/APMS/issueNews")
                        .addParams("news", JSON.toJSONString(news))
                        .tag(this)
                        .enqueue(new OkCallback<String>(new OkTextParser() {
                        }) {
                            @Override
                            public void onSuccess(int code, String flag) {
                                if (flag.equals("1")){
                                    Toasty.success(IssueNewActivity.this,"发布成功").show();
                                    finish();
                                }
                                else {
                                    Toasty.error(IssueNewActivity.this, "发布失败").show();
                                }
                            }

                            @Override
                            public void onFailure(Throwable e) {
                                Toasty.error(IssueNewActivity.this,"网络异常").show();
                                Log.e("网络异常",e.getMessage());
                                e.printStackTrace();
                            }
                        });
            }
        });
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

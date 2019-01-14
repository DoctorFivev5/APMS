package com.huangfuren.amusementparkmanagementsystem.fix;

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
import android.widget.Spinner;


import com.alibaba.fastjson.JSON;
import com.huangfuren.amusementparkmanagementsystem.R;
import com.huangfuren.amusementparkmanagementsystem.model.Fix;
import com.huangfuren.amusementparkmanagementsystem.ticket.TicketActivity;
import com.socks.okhttp.plus.OkHttpProxy;
import com.socks.okhttp.plus.callback.OkCallback;
import com.socks.okhttp.plus.parser.OkTextParser;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;

import static com.huangfuren.amusementparkmanagementsystem.MainActivity.ipAddress;

public class FixActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Spinner projectSp;
    private Spinner typeSp;
    private EditText reasonEdit;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("维修啦");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        projectSp = findViewById(R.id.project_name_fix);
        typeSp = findViewById(R.id.project_type_fix);
        reasonEdit = findViewById(R.id.project_reason_fix);
        btn = findViewById(R.id.project_fix);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String project = projectSp.getSelectedItem().toString();
                String type = typeSp.getSelectedItem().toString();
                String reason = reasonEdit.getText().toString();
                if (TextUtils.isEmpty(reason)){
                    Toasty.info(FixActivity.this,"请输入理由").show();
                    return;
                }
                Fix fix = new Fix();
                fix.setName(project);
                if (type.equals("一般")){
                    fix.setType(1);
                }else if (type.equals("加急")){
                    fix.setType(2);
                }
                OkHttpClient okHttpClient = OkHttpProxy.getInstance();
                OkHttpProxy.post()
                        .url("http://"+ipAddress+"/APMS/addFix")
                        .tag(this)
                        .addParams("fix", JSON.toJSONString(fix))
                        .enqueue(new OkCallback<String>(new OkTextParser()) {
                            @Override
                            public void onSuccess(int code, String s) {
                                if (s.equals("1")) {
                                    Toasty.success(FixActivity.this, "报修成功").show();
                                    finish();
                                }else {
                                    Toasty.success(FixActivity.this, "报修失败").show();
                                }
                            }

                            @Override
                            public void onFailure(Throwable e) {
                                Toasty.error(FixActivity.this, "网络异常").show();
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

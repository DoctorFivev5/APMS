package com.huangfuren.amusementparkmanagementsystem.ticket;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.huangfuren.amusementparkmanagementsystem.R;
import com.huangfuren.amusementparkmanagementsystem.model.News;
import com.huangfuren.amusementparkmanagementsystem.model.Ticket;
import com.huangfuren.amusementparkmanagementsystem.news.NewsActivity;
import com.socks.okhttp.plus.OkHttpProxy;
import com.socks.okhttp.plus.callback.OkCallback;
import com.socks.okhttp.plus.parser.OkJsonParser;
import com.socks.okhttp.plus.parser.OkTextParser;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;

import static com.huangfuren.amusementparkmanagementsystem.MainActivity.ipAddress;

public class TicketActivity extends AppCompatActivity implements View.OnClickListener {

    private final String intentUrl = "alipayqr://platformapi/startapp?saId=10000007&qrcode=https%3A%2F%2Fqr.alipay.com%2FFKX02944DRQERWMZHBHI79";
    private ImageView imageView;
    private TextView dateText;
    private Spinner typeText;
    private EditText nameText;
    private EditText idCardText;
    private Button button;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("在线购票");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        imageView = findViewById(R.id.ticket_image);
        dateText = findViewById(R.id.ticket_date_data);
        typeText = findViewById(R.id.ticket_type_data);
        nameText = findViewById(R.id.ticket_name_data);
        idCardText = findViewById(R.id.ticket_idcard_data);
        button = findViewById(R.id.buy);
        dateText.setOnClickListener(this);
        nameText.setOnClickListener(this);
        idCardText.setOnClickListener(this);
        button.setOnClickListener(this);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ticket_date_data:
                showDatePickerDialog();
                break;
            case R.id.buy:
                String date = dateText.getText().toString();
                String type = typeText.getSelectedItem().toString();
                String name = nameText.getText().toString();
                String idCard = idCardText.getText().toString();
                //身份证格式验证
                String idCardReg = "^(\\d{6})(\\d{4})(\\d{2})(\\d{2})(\\d{3})([0-9]|X)$";
                Pattern pattern = Pattern.compile(idCardReg);
                Matcher matcher = pattern.matcher(idCard);
                if (!matcher.matches()){
                    Toasty.error(this, "请输入正确的身份号！").show();
                    break;
                }
                Ticket ticket = new Ticket();
                ticket.setStart_time(date);
                ticket.setCreate_time(new Date());
                ticket.setState("正常");
                ticket.setType(type);
                ticket.setName(name);
                ticket.setId_card(idCard);
                ticket.setUser_id(pref.getLong("user_id",0));
                try {
                    Intent intent = Intent.parseUri(intentUrl, Intent.URI_INTENT_SCHEME);
                    startActivity(intent);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                Toasty.normal(this, "等待付款中...").show();
                OkHttpClient okHttpClient = OkHttpProxy.getInstance();
                OkHttpProxy.post()
                        .url("http://"+ipAddress+"/APMS/addTicket")
                        .tag(this)
                        .addParams("ticket", JSON.toJSONString(ticket))
                        .enqueue(new OkCallback<String>(new OkTextParser()) {
                            @Override
                            public void onSuccess(int code, String s) {
                                if (s.equals("1")) {
                                    try {
                                        //假装买票等待10s
                                        Thread.sleep(10000l);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Toasty.success(TicketActivity.this, "购票成功").show();
                                    finish();
                                }else {
                                    Toasty.success(TicketActivity.this, "购票失败").show();
                                }
                            }

                            @Override
                            public void onFailure(Throwable e) {
                                Toasty.error(TicketActivity.this, "网络异常").show();
                                Log.e("网络异常",e.getMessage());
                                e.printStackTrace();
                            }
                        });
                break;

        }
    }
    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                if (Calendar.getInstance().get(Calendar.YEAR) < year) {
                    dateText.setText(year + "-" + (month+1) + "-" + day);
                } else if (Calendar.getInstance().get(Calendar.YEAR) == year){
                    if (Calendar.getInstance().get(Calendar.MONTH) < month)
                        dateText.setText(year + "-" + (month+1) + "-" + day);
                    else if (Calendar.getInstance().get(Calendar.MONTH) == month)
                        if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) <= day)
                            dateText.setText(year + "-" + (month+1) + "-" + day);
                        else
                            dateText.setText(Calendar.getInstance().get(Calendar.YEAR) + "-" + (Calendar.getInstance().get(Calendar.MONTH)+1) + "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                    else
                        dateText.setText(Calendar.getInstance().get(Calendar.YEAR) + "-" + (Calendar.getInstance().get(Calendar.MONTH)+1) + "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                }else {
                    dateText.setText(Calendar.getInstance().get(Calendar.YEAR) + "-" + (Calendar.getInstance().get(Calendar.MONTH)+1) + "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                }
            }
        }, Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}

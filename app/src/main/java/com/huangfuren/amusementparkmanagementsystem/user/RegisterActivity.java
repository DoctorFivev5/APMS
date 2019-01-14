package com.huangfuren.amusementparkmanagementsystem.user;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.huangfuren.amusementparkmanagementsystem.R;
import com.huangfuren.amusementparkmanagementsystem.model.User;
import com.socks.okhttp.plus.OkHttpProxy;
import com.socks.okhttp.plus.callback.OkCallback;
import com.socks.okhttp.plus.parser.OkJsonParser;
import com.socks.okhttp.plus.parser.OkTextParser;

import java.text.SimpleDateFormat;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;

import static com.huangfuren.amusementparkmanagementsystem.MainActivity.ipAddress;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private EditText phoneNum;
    private ImageView deleteUserName;
    private EditText pwd;
    private ImageView deletePwd;
    private EditText rePwd;
    private ImageView deleteRePwd;
    private Button register;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }


    private void initView() {
        titleBar = findViewById(R.id.title_register);
        phoneNum = findViewById(R.id.et_register_phoneNum);
        deleteUserName = findViewById(R.id.iv_delete_register_phoneNum);
        pwd = findViewById(R.id.et_register_password);
        deletePwd = findViewById(R.id.iv_delete_register_pwd);
        rePwd = findViewById(R.id.et_register_repassword);
        deleteRePwd = findViewById(R.id.iv_delete_register_repwd);
        register = findViewById(R.id.btn_register);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        //设置监听
        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                finish();
            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onRightClick(View v) {

            }
        });
        deleteUserName.setOnClickListener(this);
        deletePwd.setOnClickListener(this);
        deleteRePwd.setOnClickListener(this);
        register.setOnClickListener(this);
        deleteUserNameListener();
        deletepwdListener();
        deleteRePwdListener();
    }
    /**
     * @Description:  设置用户名编辑框监听
     */
    private void deleteUserNameListener() {
        phoneNum.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (temp.length() > 0){
                    deleteUserName.setVisibility(View.VISIBLE);
                }else {
                    deleteUserName.setVisibility(View.GONE);
                }
            }
        });
    }
    /**
     * @Description:  设置密码编辑框监听
     */
    private void deletepwdListener(){
        pwd.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (temp.length() > 0){
                    deletePwd.setVisibility(View.VISIBLE);
                }else {
                    deletePwd.setVisibility(View.GONE);
                }
            }
        });
    }
    /**
     设置确认密码编辑框监听
     */
    private void deleteRePwdListener() {
        rePwd.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (temp.length() > 0){
                    deleteRePwd.setVisibility(View.VISIBLE);
                }else {
                    deleteRePwd.setVisibility(View.GONE);
                }
            }
        });
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_delete_register_phoneNum:
                phoneNum.setText("");
                break;
            case R.id.iv_delete_register_pwd:
                pwd.setText("");
                break;
            case R.id.iv_delete_register_repwd:
                rePwd.setText("");
                break;
            case R.id.btn_register:
                if (phoneNum.getText().toString().equals("")){
                    Toast.makeText(RegisterActivity.this,"请输入用户名！",Toast.LENGTH_SHORT).show();
                    phoneNum.requestFocus();
                }else if (pwd.getText().toString().equals("")){
                    Toast.makeText(RegisterActivity.this,"请输入密码！",Toast.LENGTH_SHORT).show();
                    pwd.requestFocus();
                }else if (rePwd.getText().toString().equals("")){
                    Toast.makeText(RegisterActivity.this,"请输入确认密码！",Toast.LENGTH_SHORT).show();
                    rePwd.requestFocus();
                }else if (!pwd.getText().toString().equals(rePwd.getText().toString())){
                    Toast.makeText(RegisterActivity.this,"两次密码不同，请重新输入！",Toast.LENGTH_SHORT).show();
                    pwd.setText("");
                    rePwd.setText("");
                    pwd.requestFocus();
                }else {
                    OkHttpClient okHttpClient = OkHttpProxy.getInstance();
                    OkHttpProxy.post()
                            .url("http://"+ipAddress+"/APMS/register")
                            .tag(this)
                            .addParams("user", JSON.toJSONString(new User(phoneNum.getText().toString(), pwd.getText().toString())))
                            .enqueue(new OkCallback<String>(new OkTextParser() {
                            }) {
                                @Override
                                public void onSuccess(int code,String flag) {
                                    if (flag.equals("1")){
                                        Toasty.success(RegisterActivity.this,"注册成功").show();
                                        finish();
                                    }
                                    else {
                                        Toasty.error(RegisterActivity.this, "该用户名已注册").show();
                                    }
                                }

                                @Override
                                public void onFailure(Throwable e) {
                                    Log.e("网络异常",e.getMessage());
                                    e.printStackTrace();
                                }
                            });
                    Toasty.error(RegisterActivity.this, "注册中...").show();
                }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpProxy.cancel(this);
    }
}

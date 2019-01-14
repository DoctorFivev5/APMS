package com.huangfuren.amusementparkmanagementsystem.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.huangfuren.amusementparkmanagementsystem.MainActivity;
import com.huangfuren.amusementparkmanagementsystem.R;
import com.huangfuren.amusementparkmanagementsystem.dao.UserDao;
import com.huangfuren.amusementparkmanagementsystem.model.News;
import com.huangfuren.amusementparkmanagementsystem.model.User;
import com.huangfuren.amusementparkmanagementsystem.news.NewsActivity;
import com.socks.okhttp.plus.OkHttpProxy;
import com.socks.okhttp.plus.callback.OkCallback;
import com.socks.okhttp.plus.parser.OkJsonParser;

import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;

import static com.huangfuren.amusementparkmanagementsystem.MainActivity.ipAddress;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TitleBar titleBar;
    private EditText accountEdit;
    private EditText passwordEdit;
    private ImageView hideOrShowImage;
    private ImageView deleteAccount;
    private ImageView deletePwd;
    private CheckBox rememberPassword;
    private Button loginBut;
    private TextView register;
    private TextView forgetPwd;
    private Boolean isRemember;
    private Boolean isAutoLogin;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();

    }


    private void initView() {
        titleBar = findViewById(R.id.title_login);
        accountEdit = findViewById(R.id.et_account);
        passwordEdit = findViewById(R.id.et_password);
        hideOrShowImage = findViewById(R.id.iv_see_password);
        deleteAccount = findViewById(R.id.iv_delete_username);
        deletePwd = findViewById(R.id.iv_delete_pwd);
        rememberPassword = findViewById(R.id.checkBox_password);
        loginBut = findViewById(R.id.btn_login);
        register = findViewById(R.id.tv_register);
        forgetPwd = findViewById(R.id.tv_forget_pwd);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String account = pref.getString("user_name","");
        accountEdit.setText(account);
        //实现记住密码功能
        isRemember = pref.getBoolean("remember_password",false);
        //实现自动登录功能
        isAutoLogin = pref.getBoolean("auto_login",false);
        if (isAutoLogin){
            //创建一个意图
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            editor = pref.edit();
            editor.putBoolean("logining",true);
            editor.apply();
            finish();
        }else if (isRemember){
            String phoneNum = pref.getString("phoneNum","");
            accountEdit.setText(phoneNum);
            String password = pref.getString("password","");
            passwordEdit.setText(password);
            rememberPassword.setChecked(true);
            accountEdit.setSelection(accountEdit.getText().length());
        }

        //设置点击事件
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

        hideOrShowImage.setOnClickListener(this);
        loginBut.setOnClickListener(this);
        register.setOnClickListener(this);
        forgetPwd.setOnClickListener(this);
        deleteAccount.setOnClickListener(this);
        deletePwd.setOnClickListener(this);
        accountListener();
        pwdListener();
    }
    /**
     * 账户文本框监听
     */
    private void accountListener() {
        accountEdit.addTextChangedListener(new TextWatcher() {
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
                    deleteAccount.setVisibility(View.VISIBLE);
                }else {
                    deleteAccount.setVisibility(View.GONE);
                }
            }
        });
    }
    /**
     * @Author:  Infinity
     * @Date:  2018/11/30 0030
     * @Description:  密码文本框监听
     */
    private void pwdListener() {
        passwordEdit.addTextChangedListener(new TextWatcher() {
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


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_see_password:
                if (hideOrShowImage.isSelected()) {
                    hideOrShowImage.setSelected(false);
                    //密码不可见
                    passwordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    //光标移到行尾
                    passwordEdit.setSelection(passwordEdit.getText().length());
                } else {
                    hideOrShowImage.setSelected(true);
                    //密码可见
                    passwordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    //光标移到行尾
                    passwordEdit.setSelection(passwordEdit.getText().length());
                }
                break;

            case R.id.iv_delete_username:
                accountEdit.setText("");
                break;

            case R.id.iv_delete_pwd:
                passwordEdit.setText("");
                break;

            case R.id.btn_login:
                final String account = accountEdit.getText().toString();
                final String password = passwordEdit.getText().toString();
                if(account.equals("")){
                    Toasty.info(LoginActivity.this,"请输入账号！").show();
                    accountEdit.requestFocus();
                }else if (password.equals("")){
                    Toasty.info(LoginActivity.this,"请输入密码！").show();
                    passwordEdit.requestFocus();
                }else {
                    OkHttpClient okHttpClient = OkHttpProxy.getInstance();
                    OkHttpProxy.post()
                            .url("http://"+ipAddress+"/APMS/login")
                            .tag(this)
                            .addParams("user", JSON.toJSONString(new User(account, password)))
                            .enqueue(new OkCallback<User>(new OkJsonParser<User>() {
                            }) {
                                @Override
                                public void onSuccess(int code,User user) {
                                    if (null != user){
                                        editor = pref.edit();
                                        editor.putLong("user_id",user.getId());
                                        editor.putString("phoneNum",account);
                                        editor.putString("username", user.getUsername());
                                        editor.putBoolean("logining",true);
                                        editor.putString("password",password);
                                        editor.putString("user_icon",user.getUserIcon());
                                        editor.putInt("role",user.getRole());
                                        if (rememberPassword.isChecked()){
                                            editor.putBoolean("remember_password",true);
//                                            editor.putBoolean("auto_login",true);
                                        }else {
                                            editor.putBoolean("remember_password",false);
//                                            editor.putBoolean("auto_login",false);
                                        }
                                        editor.apply();
                                        Toasty.success(LoginActivity.this,"登录成功").show();
                                        finish();
                                    }
                                    else {
                                        Toasty.error(LoginActivity.this, "账号或密码错误").show();
                                        passwordEdit.setText("");
                                        passwordEdit.requestFocus();
                                    }
                                }

                                @Override
                                public void onFailure(Throwable e) {
                                    Toasty.error(LoginActivity.this, "账号或密码错误").show();
                                    e.printStackTrace();
                                }
                            });
                    Toasty.normal(this, "登录中...").show();
                }
                break;

            case R.id.tv_register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forget_pwd:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpProxy.cancel(this);
    }
}

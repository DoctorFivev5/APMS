package com.huangfuren.amusementparkmanagementsystem;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.huangfuren.amusementparkmanagementsystem.adapter.MainAdapter;
import com.huangfuren.amusementparkmanagementsystem.common.RecyclerItemDecoration;
import com.huangfuren.amusementparkmanagementsystem.model.Main;
import com.huangfuren.amusementparkmanagementsystem.user.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    public static final String ipAddress = "192.168.1.103:8080";
    private final int SDK_PERMISSION_REQUEST = 127;
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private LinearLayout headerLayout;
    private ImageView userIcon;
    private TextView username;
    private MainAdapter adapter;
    private List<Main> mainList = new ArrayList<>();;
    private String permissionInfo;
    private SharedPreferences sp;
    private boolean logining;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getPersimmions();
        initView();
        initData();
    }

    private void initView(){
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        logining = sp.getBoolean("logining", false);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }
        mRecyclerView = findViewById(R.id.main_recycler);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        headerLayout = (LinearLayout) navigationView.getHeaderView(0);
        userIcon = headerLayout.findViewById(R.id.user_icon);
        username = headerLayout.findViewById(R.id.user_name);
        userIcon.setOnClickListener(this);
        username.setOnClickListener(this);
    }

    private void initData(){
        setSupportActionBar(toolbar);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        Log.e("url","http://"+ipAddress+sp.getString("user_icon",""));
        loadUser();
        initList();
        adapter = new MainAdapter(mainList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new RecyclerItemDecoration(10,2));
        mRecyclerView.setAdapter(adapter);
    }

    private void loadUser() {
        logining = sp.getBoolean("logining", false);
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.app_icon)//图片加载前显示的图片
                .fallback(R.drawable.app_icon)//url为空显示的图片
                .error(R.drawable.app_icon)//图片加载失败后显示的图片
                .circleCropTransform()//圆形边框
                .override(200,200);
        if (logining) {
            Glide.with(this).load("http://" + ipAddress + sp.getString("user_icon", "")).apply(options).into(userIcon);
            username.setText(sp.getString("username", "未登录"));
        }else {
            Glide.with(this).load(R.drawable.app_icon).apply(options).into(userIcon);
            username.setText("未登录");
        }
    }

    private void initList() {
        mainList.clear();
        mainList.add(new Main("新闻活动", R.drawable.news));
        if (PreferenceManager.getDefaultSharedPreferences(this).getInt("role",0)==2){
            mainList.add(new Main("维修", R.drawable.fix));
        }else {
            mainList.add(new Main("购票", R.drawable.ticket));
        }
        mainList.add(new Main("地图", R.drawable.map));
        mainList.add(new Main("排队", R.drawable.queue));
        if (adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == android.R.id.home){
            drawer.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_gallery) {

        } else if (id == R.id.logon) {
            if (sp.getBoolean("logining",false)) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("logining", false);
                editor.putLong("user_id",0);
                editor.putString("username", "");
                editor.putString("user_icon","");
                editor.putInt("role",0);
                editor.apply();
                loadUser();
                initList();
                Toasty.info(this, "已登出").show();
            }else {
                Toasty.info(this, "请登录").show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
			/*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }

        }else{
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id==R.id.user_icon){
            if (sp.getBoolean("logining",false)) {
//                SharedPreferences.Editor editor = sp.edit();
//                editor.putBoolean("logining", false);
//                editor.apply();
            }else {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        }
        if (id==R.id.user_name){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUser();
        initList();
    }
}

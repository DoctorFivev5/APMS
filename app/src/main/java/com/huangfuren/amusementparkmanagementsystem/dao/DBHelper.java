package com.huangfuren.amusementparkmanagementsystem.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Message;


import com.huangfuren.amusementparkmanagementsystem.model.User;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by DoctorFive on 2017/11/21.
 * 数据库操作类
 */


public class DBHelper {
    private static final String DATABASE_NAME = "datastorage1";// 保存数据库名称
    private static final int DATABASE_VERSION = 2;// 保存数据库版本号
    private static final String[] USER_COLUMNS = { "id","username", "password", "phoneNum" , "sex", "role", "userIcon"};
//    private static final String[] NEWS_COLUMNS = {"id", "title", "body", "type", "start_time", "end_time", "user_id"};
//    private static final String[] TICKET_COLUMNS = {"id", "type", "state", "create_time", "end_time", "user_id"};
    private static User myUser;
    private DBListener myDBListener;
    private DBOpenHelper helper;
    private SQLiteDatabase db;
    private String servicesIP = "47.100.162.55:8080";
    //private String servicesIP = "192.168.42.212";
    //private String servicesIP = "192.168.1.102";




    static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 111:
                    myUser = (User) msg.obj;
                    break;
            }
        }
    }
    MyHandler myHandler = new MyHandler();


    private Request request;//网页返回结果
    public static final MediaType TYPE_OF_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType TYPE_OF_PNG = MediaType.parse("image/png");
    private static class DBOpenHelper extends SQLiteOpenHelper {
        //定义user表sql语句
        private static final String CREATE_TABLE_USER = "create table t_user"
            + " ( " + USER_COLUMNS[0] + " bigint(20) primary key autoincrement, "
            + USER_COLUMNS[1] + " varchar(256) ,"
            + USER_COLUMNS[2] + " varchar(16) , "
            + USER_COLUMNS[3] + " varchar(15) , "
            + USER_COLUMNS[4] + " char(4), "
            + USER_COLUMNS[5] + " varchar(1) , "
            + USER_COLUMNS[6] + " varchar(256) );";


        //定义news表
//        private static final String CREATE_TABLE_NEWS = "create table t_news"
//                + " ( " + NEWS_COLUMNS[0] + " bigint(20) primary key autoincrement, "
//                + NEWS_COLUMNS[1] + " varchar(256) ,"
//                + NEWS_COLUMNS[2] + " text ,"
//                + NEWS_COLUMNS[3] + " smallint(1) ,"
//                + NEWS_COLUMNS[4] + " timestamp ,"
//                + NEWS_COLUMNS[5] + " timestamp ,"
//                + NEWS_COLUMNS[6] + " bigint(20) );";
        public DBOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE_USER);// 创建User表格
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    public DBHelper(Context context){
        helper = new DBOpenHelper(context);// 创建SQLiteOpenHelper对象
        db = helper.getWritableDatabase();// 获得可写的数据库
    }

    public DBHelper(Context context, DBListener DBListener){
        helper = new DBOpenHelper(context);// 创建SQLiteOpenHelper对象
        db = helper.getWritableDatabase();// 获得可写的数据库
        myDBListener = DBListener;
    }




    /**
     * 外部调用DBHelper的网络操作 需要实现的回调接口
     */
    public interface DBListener {
        void doNetRequestChange(Object object);

    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }
}

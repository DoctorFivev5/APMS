package com.huangfuren.amusementparkmanagementsystem.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.huangfuren.amusementparkmanagementsystem.model.User;

/**
 * Created by DoctorFive on 2018/12/14.
 */

public class UserDao {
    private DBHelper myDBHelper;
    private ContentValues values;
    private SQLiteDatabase db;

    public UserDao(Context context) {
        this.myDBHelper = new DBHelper(context);
        db = myDBHelper.getDb();
    }

    /**
     * 插入用户
     * @param user
     */
    public void add(User user){
        values = new ContentValues();
        values.put("username",user.getUsername());
        values.put("password",user.getPassword());
        values.put("phoneNum",user.getPhoneNum());
        values.put("sex",user.getSex());
        values.put("role",user.getRole());
        values.put("userIcon",user.getUserIcon());
        db.insert("t_user",null,values);
    }

    /**
     * 更新用户信息
     * @param user
     */
    public void update(User user){
        values = new ContentValues();
        values.put("username",user.getUsername());
        values.put("password",user.getPassword());
        values.put("phoneNum",user.getPhoneNum());
        values.put("sex",user.getSex());
        values.put("role",user.getRole());
        values.put("userIcon",user.getUserIcon());
        db.update("t_user",values,"_id = ?",new String[]{String.valueOf(user.getId())});
    }

    /**
     * 根据用户名密码查询用户
     * @param username
     * @param password
     * @return
     */
    public User query(String username, String password){
        Cursor cursor = db.query("t_user",null,"name = ? and pwd = ?",new String[]{username, password},
                null,null,null);
        if (cursor.moveToNext()){
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            user.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user.setPhoneNum(cursor.getString(cursor.getColumnIndex("phoneNum")));
            user.setSex(cursor.getString(cursor.getColumnIndex("sex")));
            user.setRole(cursor.getInt(cursor.getColumnIndex("role")));
            user.setUserIcon(cursor.getString(cursor.getColumnIndex("userIcon")));
            return user;
        }
        return null;// 如果没有信息，则返回null
    }
}

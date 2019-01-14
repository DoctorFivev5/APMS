package com.huangfuren.amusementparkmanagementsystem.model;

import java.io.Serializable;

/**
 * Created by DoctorFive on 2018/12/14.
 */

public class User implements Serializable {
    private long id;
    private String username;
    private String password;
    private String phoneNum;
    private String sex;
    private int role;
    private String userIcon;

    public User(){}

    public User(String phoneNum, String password){
        this.phoneNum = phoneNum;
        this.password = password;
    }

    public User(long id, String username, String password, String phoneNum, String sex, int role, String userIcon) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.phoneNum = phoneNum;
        this.sex = sex;
        this.role = role;
        this.userIcon = userIcon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }
}

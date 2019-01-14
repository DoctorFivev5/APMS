package com.huangfuren.amusementparkmanagementsystem.model;

/**
 * Created by DoctorFive on 2018/12/17.
 */

public class Main {
    private String title;
    private int imageId;

    public Main(String title, int imageId) {
        this.title = title;
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}

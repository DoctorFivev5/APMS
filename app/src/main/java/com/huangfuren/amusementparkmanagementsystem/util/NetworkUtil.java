package com.huangfuren.amusementparkmanagementsystem.util;


import com.socks.okhttp.plus.OkHttpProxy;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import okhttp3.OkHttpClient;

/**
 * Created by DoctorFive on 2018/12/18.
 */

public class NetworkUtil {
//    OkHttpClient okHttpClient = OkHttpProxy.getInstance();
//
//    //ignore HTTPS Authentication
//        okHttpClient.setHostnameVerifier(new MyHostnameVerifier());
//        try {
//        SSLContext sc = SSLContext.getInstance("TLS");
//        sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
//        okHttpClient.setSslSocketFactory(sc.getSocketFactory());
//    } catch (NoSuchAlgorithmException e) {
//        e.printStackTrace();
//    } catch (KeyManagementException e) {
//        e.printStackTrace();
//    }
}

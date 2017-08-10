package com.coolweather.android.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static android.R.string.ok;

/**
 * Created by ${justin} on 2017/8/1015: 19
 * WeChat:Justin-Tz
 */
//网络工具
public class HttpUtil {
    //发起请求
    public static void sendOkhttpReauest(String address , okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

}

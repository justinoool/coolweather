package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ${justin} on 2017/8/1109: 58
 * WeChat:Justin-Tz
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public  String info;
    }
}

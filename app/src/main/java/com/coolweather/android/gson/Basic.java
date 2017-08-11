package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ${justin} on 2017/8/1021: 49
 * WeChat:Justin-Tz
 */

public class Basic {
    @SerializedName("city")//注解
    public String cityName;
    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;

    }

}

package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ${justin} on 2017/8/1110: 03
 * WeChat:Justin-Tz
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort {
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("txt")
        public String info;
    }

   public class Sport {
        @SerializedName("txt")
        public String info;
    }
}

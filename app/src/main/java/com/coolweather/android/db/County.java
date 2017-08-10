package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by ${justin} on 2017/8/1014: 48
 * WeChat:Justin-Tz
 */
//县
public class County extends DataSupport {
    private int id;
    private String countyName;
    private String weatherId; //记录县所对应的天气
    private int cityId;//记录当前县所属市的Id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}

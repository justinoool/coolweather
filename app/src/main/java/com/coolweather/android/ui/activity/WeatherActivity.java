package com.coolweather.android.ui.activity;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.R;
import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;


/*
    注意:有时候，我们需要在一个Activity中调用另一个xml布局文件，
    即非本Activity所绑定的xml布局文件中的控件，这时候就不能直接findViewById，
    不然会报错指向空对象，这时就需要像下面这样做。
        LayoutInflater factory = LayoutInflater.from(当前类.this);
        View layout = factory.inflate(R.layout.你要获取的另一个XML, null);
        TextView textview = (TextView) layout.findViewById(R.id.控件ID);
        一行写完就是：
        TextView textview = (TextView) LayoutInflater.from(当前类.this)
        .inflate(R.layout.你要获取的另一个XML, null)
        .findViewById(R.id.控件ID);
 */

public class WeatherActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private Button navButton;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //有机会设置兼容5.0以下
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){//5.0版本以上
            Window window = getWindow();
            *//*
            清除一些标记FLAG_TRANSLUCENT_STATUS | FLAG_TRANSLUCENT_NAVIGATION 4.4才用
                    *//*
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | FLAG_TRANSLUCENT_NAVIGATION);
            View decorView = window.getDecorView();
            *//*
            decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
            通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。间接可以计算状态栏高度。
            *//*
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LAYOUT_STABLE );
            *//**//* 设置了FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,表明会Window负责系统bar的background 绘制，绘制透明背景的系统bar（状态栏和导航栏）*//**//*
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            //  window.setNavigationBarColor(Color.TRANSPARENT);//标题栏透明
        }else if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP){}*/

        /*    沉浸状态栏 做笔记用
        if (Build.VERSION.SDK_INT >= 21){
            Window window = getWindow();
            window.clearFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS );
            window.setStatusBarColor( Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }*/

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);
        // 初始化各控件
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        final String weatherId;
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            // 无缓存时去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);//请求数据要将ScrollView隐藏，不然空数据界面不雅观
            requestWeather(weatherId);//请求数据
        }
        //刷新
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
        //打开滑动菜单
       navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
    }

    /*
    根据天气id请求城市天气信息
   */
    public void requestWeather(final String weatherId) {
        //接口地址
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=74fca083630f4d5599f11d72b9099808";
        HttpUtil.sendOkhttpReauest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);//显示到界面
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);//结束刷新并隐藏
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);//结束刷新并隐藏
                    }
                });
            }
        });
        loadBingPic();
    }

    /*
     加载必应每日图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkhttpReauest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager   //存到数据库中
                        .getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    /*
    处理并展示Weather实体类中的数据
    */
    private void showWeatherInfo(Weather weather) {
        if(weather != null && "ok".equals(weather.status)){
            String cityName = weather.basic.cityName;
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            String degree = weather.now.temperature + "℃";
            String weatherInfo = weather.now.more.info;
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);
            forecastLayout.removeAllViews();
            for (Forecast forecast : weather.forecastList) {
                View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
                TextView dateText = (TextView) view.findViewById(R.id.date_text);
                TextView infoText = (TextView) view.findViewById(R.id.info_text);
                TextView maxText = (TextView) view.findViewById(R.id.max_text);
                TextView minText = (TextView) view.findViewById(R.id.min_text);
                dateText.setText(forecast.date);
                infoText.setText(forecast.more.info);
                maxText.setText(forecast.temperature.max);
                minText.setText(forecast.temperature.min);
                forecastLayout.addView(view);
            }
            if (weather.aqi != null) {
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
            }
            String comfort = "舒适度：" + weather.suggestion.comfort.info;
            String carWash = "洗车指数：" + weather.suggestion.carWash.info;
            String sport = "运行建议：" + weather.suggestion.sport.info;
            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
            weatherLayout.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        }else{
            Toast.makeText(this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
        }
       
    }

}

package com.example.hyunju.notification_collector.global;

import android.app.Application;

import com.example.hyunju.notification_collector.telegram.TgHelper;
import com.example.hyunju.notification_collector.utils.NetworkService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GlobalApplication extends Application {
    public static NetworkService service;

    static GlobalApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        TgHelper.init(this);
        mApplication = this;
//        retrofitInit();
    }

    public static GlobalApplication getInstance(){
        return mApplication;
    }

    private static void retrofitInit() {
        OkHttpClient client =
                new OkHttpClient.Builder()
                        .connectTimeout(100, TimeUnit.SECONDS)
                        .readTimeout(100, TimeUnit.SECONDS).build();

        Retrofit retrofit =
                new Retrofit
                        .Builder()
                        .baseUrl("http://test.com") // 일단 server의 return값으로
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

        service = retrofit.create(NetworkService.class);
    }
}

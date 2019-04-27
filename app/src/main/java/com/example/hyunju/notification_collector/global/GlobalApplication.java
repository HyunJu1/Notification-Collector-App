package com.example.hyunju.notification_collector.global;

import android.app.Application;

import com.example.hyunju.notification_collector.utils.NetworkService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GlobalApplication extends Application {
    public static NetworkService service;

    @Override
    public void onCreate() {
        super.onCreate();
//        retrofitInit();
    }

    private static void retrofitInit() {
        Retrofit retrofit =
                new Retrofit
                        .Builder()
//                        .baseUrl("") // 일단 server의 return값으로
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

        service = retrofit.create(NetworkService.class);
    }
}

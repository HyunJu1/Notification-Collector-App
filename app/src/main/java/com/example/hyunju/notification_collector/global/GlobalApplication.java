package com.example.hyunju.notification_collector.global;

import android.app.Application;

import com.example.hyunju.notification_collector.models.Contact;
import com.example.hyunju.notification_collector.models.NotificationEvent;
import com.example.hyunju.notification_collector.models.SendedMessage;
import com.example.hyunju.notification_collector.telegram.TgHelper;
import com.example.hyunju.notification_collector.utils.NetworkService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GlobalApplication extends Application {
    public static NetworkService service;

    static GlobalApplication mApplication;

    public static boolean isMultiMode = false;
    public static List<SendedMessage> sendedMessageInMultiMode = new ArrayList<>();
    public static List<NotificationEvent> receivedMessageInMultiMode = new ArrayList<>();
    public static ArrayList<Contact> selectedContactsInMultiMode = new ArrayList<>();

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

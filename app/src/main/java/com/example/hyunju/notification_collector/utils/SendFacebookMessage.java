package com.example.hyunju.notification_collector.utils;

import android.util.Log;

import com.example.hyunju.notification_collector.global.GlobalApplication;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendFacebookMessage {
    private Call<JsonObject> sendFacebookMessage;
    private String recipient;
    private String content;

    public SendFacebookMessage(String recipient, String content) {
        this.recipient = recipient;
        this.content = content;
    }

    public void execute() {
        sendFacebookMessage = GlobalApplication.service.sendMessage(
                this.recipient,
                this.content
        );

        sendFacebookMessage.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("facebook", response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("facebook", t.toString());
            }
        });
    }
}

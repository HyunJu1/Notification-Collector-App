package com.example.hyunju.notification_collector.utils;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NetworkService {
    @POST("/sendMessage")
    Call<JsonObject> sendMessage(@Query("recipient") String recipient, @Query("content") String content);
}

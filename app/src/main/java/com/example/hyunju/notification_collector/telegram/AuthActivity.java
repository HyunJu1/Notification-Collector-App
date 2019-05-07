package com.example.hyunju.notification_collector.telegram;

import android.os.Bundle;

import com.example.hyunju.notification_collector.global.CollectorActivity;


public class AuthActivity extends CollectorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAuth();
    }
}

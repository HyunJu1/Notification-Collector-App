package com.example.hyunju.notification_collector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.example.hyunju.notification_collector.global.CollectorActivity;


public class SplashActivity extends CollectorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // 어플 잠금 설정이 되어있는 경우
        // 일단 개발하느라 불편하므로 주석처리 하겠음
//        if(sharedPreferences.getBoolean("pref_lock", false) == true) {
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent intent = new Intent(SplashActivity.this, LockActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }, 1000);
//        } else { // 어플 잠금 설정이 되어있지 않는 경우
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
            /** 빠른 디버깅을 위해 일단 1초로 설정. 추후 변동 가능
             *
             */
//        }
    }

}

package com.example.hyunju.notification_collector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.hyunju.notification_collector.global.CollectorActivity;
import com.example.hyunju.notification_collector.utils.fingerprint.BiometricCallback;
import com.example.hyunju.notification_collector.utils.fingerprint.BiometricManager;


public class SplashActivity extends CollectorActivity implements BiometricCallback {

    private BiometricManager biometricManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // 어플 잠금 설정이 되어있는 경우
        // 일단 개발하느라 불편하므로 주석처리 하겠음
        if (sharedPreferences.getBoolean("pref_lock", false) == true) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    biometricManager = new BiometricManager.BiometricBuilder(SplashActivity.this)
                            .setTitle(getString(R.string.biometric_title))
                            .setDescription(getString(R.string.biometric_description))
                            .setNegativeButtonText(getString(R.string.biometric_negative_button_text))
                            .build();

                    biometricManager.authenticate(SplashActivity.this);
//                    Intent intent = new Intent(SplashActivity.this, LockActivity.class);
//                    startActivity(intent);
//                    finish();
                }
            }, 1000);
        } else { // 어플 잠금 설정이 되어있지 않는 경우
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

    @Override
    public void onSdkVersionNotSupported () {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_error_sdk_not_supported), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(SplashActivity.this, LockActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBiometricAuthenticationNotSupported () {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_error_hardware_not_supported), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(SplashActivity.this, LockActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBiometricAuthenticationNotAvailable () {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_error_fingerprint_not_available), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(SplashActivity.this, LockActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBiometricAuthenticationPermissionNotGranted () {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_error_permission_not_granted), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBiometricAuthenticationInternalError (String error){
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationFailed () {
//        Toast.makeText(getApplicationContext(), getString(R.string.biometric_failure), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationCancelled () {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_cancelled), Toast.LENGTH_LONG).show();
//        BiometricManager.cancelAuthentication();
    }

    @Override
    public void onAuthenticationSuccessful () {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_success), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAuthenticationHelp ( int helpCode, CharSequence helpString){
//        Toast.makeText(getApplicationContext(), helpString, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationError ( int errorCode, CharSequence errString){
//        Toast.makeText(getApplicationContext(), errString, Toast.LENGTH_LONG).show();
    }
}

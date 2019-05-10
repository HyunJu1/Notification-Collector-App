package com.example.hyunju.notification_collector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

public class LockActivity extends AppCompatActivity {

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private final static String TAG = LockActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lock);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);

        mPinLockView.attachIndicatorDots(mIndicatorDots);

        mPinLockView.setPinLength(4);

        mPinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                Log.d(TAG, "lock code: " + pin);
                Log.e("sfasdf", sharedPreferences.getString("pref_pw", "1234"));

                if (pin.equals(sharedPreferences.getString("pref_pw", "1234"))) {
                    Intent intent = new Intent(LockActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LockActivity.this, "비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onEmpty() {
                Log.d(TAG, "비밀번호를 입력해주세요.");
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
                Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
            }
        });
    }
}
package com.example.hyunju.notification_collector.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.hyunju.notification_collector.models.NotificationEvent;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SMSBroadcastReceiver extends BroadcastReceiver {
    private Bundle bundle;
    private SmsMessage currentSMS;
    private String message;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("onReceive()","부팅완료");
            //Intent i = new Intent(context, ScreenService.class);
            //context.startService(i);
        }

        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.d("onReceive()","스크린 ON");
        }

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.d("onReceive()","스크린 OFF");
        }

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Log.d("SMSBroadcastReceiver", "SMS 메시지가 수신되었습니다.");
            Toast.makeText(context, "문자가 수신되었습니다", Toast.LENGTH_SHORT).show();
            bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                if (pdu_Objects != null) {
                    for (Object aObject : pdu_Objects) {
                        currentSMS = getIncomingMessage(aObject, bundle);
                        String senderNo = currentSMS.getDisplayOriginatingAddress();
                        message = currentSMS.getDisplayMessageBody();
                        Date curDate = new Date(currentSMS.getTimestampMillis());
                        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
                        String receivedDate = sdf.format( curDate );

                        NotificationEvent notificationEvent = new NotificationEvent(senderNo, message, receivedDate);
                        EventBus.getDefault().post(notificationEvent);

                        Intent msgrcv = new Intent("SMS");

                        msgrcv.putExtra("senderNo", senderNo);
                        msgrcv.putExtra("message", message);
                        msgrcv.putExtra("receivedDate",receivedDate);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);

                        Toast.makeText(context, "senderNum: " + senderNo + " :\n message: " + message +" \n time:"+receivedDate, Toast.LENGTH_LONG).show();


                    }
                    this.abortBroadcast();
                    // End of loop
                }
            }
        } // bundle null
    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }
}


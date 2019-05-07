package com.example.hyunju.notification_collector.utils;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.hyunju.notification_collector.models.NotificationEvent;
import com.example.hyunju.notification_collector.models.SendedMessage;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationListener extends NotificationListenerService {
    private final static String TAG = NotificationListener.class.getName();

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date date = new Date(sbn.getPostTime());
        String formattedDate = sdf.format(date);

        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;

        String title = extras.getString(Notification.EXTRA_TITLE);
        CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
      //  CharSequence postTime = extras.getCharSequence(Notification.EXTRA_);

        int smallIconRes = extras.getInt(Notification.EXTRA_SMALL_ICON);
        Bitmap largeIcon = extras.getParcelable(Notification.EXTRA_LARGE_ICON);

        NotificationEvent notificationEvent = new NotificationEvent(title, text, subText, formattedDate);
        Log.i(TAG, notificationEvent.toString());

        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);
        msgrcv.putExtra("subText", subText);
       // msgrcv.putExtra("postTime", postTime);
        EventBus.getDefault().post(notificationEvent);

    }
}

package com.example.hyunju.notification_collector.utils;

import android.app.Notification;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.hyunju.notification_collector.models.NotificationEvent;

import org.greenrobot.eventbus.EventBus;

public class NotificationListener extends NotificationListenerService {
    private final static String TAG = NotificationListener.class.getName();

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
//        Log.i(TAG, "onNotificationPosted() - " + sbn.toString());
//        Log.i(TAG, "PackageName:" + sbn.getPackageName());
        Log.i(TAG, "PostTime:" + sbn.getPostTime());

        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;

        String title = extras.getString(Notification.EXTRA_TITLE);
        CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
        int smallIconRes = extras.getInt(Notification.EXTRA_SMALL_ICON);
        Bitmap largeIcon = extras.getParcelable(Notification.EXTRA_LARGE_ICON);

        NotificationEvent notificationEvent = new NotificationEvent(title, text, subText);
        Log.i(TAG, notificationEvent.toString());
        EventBus.getDefault().post(notificationEvent);
    }
}

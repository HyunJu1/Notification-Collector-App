package com.example.hyunju.notification_collector.database;

import android.content.Context;

public class DataManager {
    Context context = null;
    UserDBHelper userDBHelper;
    ContactDBHelper contactDBHelper;
    MessageDBHelper messageDBHelper;

    public DataManager(Context aContext) {
        context = aContext;
        userDBHelper = new UserDBHelper(context);
        contactDBHelper = new ContactDBHelper(context);
        messageDBHelper = new MessageDBHelper(context);

    }

    /**
     * DB DML 관련 함수 작성은 여기에
     *
     */
}

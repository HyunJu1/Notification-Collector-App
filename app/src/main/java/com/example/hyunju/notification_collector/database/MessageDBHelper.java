package com.example.hyunju.notification_collector.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessageDBHelper  extends SQLiteOpenHelper {
    public MessageDBHelper(Context context) {
        super(context, "message.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

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
        db.execSQL("create table message(" +
                " message_id Integer primary key," +
                " message_title TEXT ," +
                " message_body TEXT ," +
                " create_time DATE not null," +
                " creator_id TEXT ," +
                " recipent_id TEXT ," +
                " platform TEXT not null," +
                " mailtype TEXT , " +
                " type  Integer not null ," +
                " isReply  Integer " +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

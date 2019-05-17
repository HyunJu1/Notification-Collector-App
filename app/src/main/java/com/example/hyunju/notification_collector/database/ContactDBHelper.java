package com.example.hyunju.notification_collector.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactDBHelper extends SQLiteOpenHelper {

    /**
     * Contact DB는 굳이 필요없을것 같다. 그래서 추후에 필요하게 되면 그때 연동
     */


    public ContactDBHelper(Context context) {
        super(context, "contact.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table contact(" +
                " contact_id  INTEGER PRIMARY KEY AUTOINCREMENT," +
                " phonenum TEXT ," +
                " name TEXT ," +
                " email TEXT," +
                " photo_id TEXT" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

package com.example.hyunju.notification_collector.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.hyunju.notification_collector.models.SendedMessage;

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


    public void smsInsert(SendedMessage sendedMessage) {
        SQLiteDatabase messageDB = messageDBHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("message_body",sendedMessage.message); // sms 에선 메세지를 body에 저장
        cv.put("create_time",sendedMessage.time);
        cv.put("platform",sendedMessage.platform);
        cv.put("type",sendedMessage.type);
        cv.put("recipent_phoneNum",sendedMessage.recipent_phoneNum);
        messageDB.insert("message",null,cv);
        Log.d("DBDB", "성공적으로 데이터 삽입");

    }


    public Cursor smsReader(String phonenum) {
        SQLiteDatabase messageDB = messageDBHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM message where recipent_phoneNum= '"+ phonenum+"'"; // 따옴 표
        Cursor cursor = messageDB.rawQuery(selectQuery, null);
        Log.d("cursor개수", String.valueOf(cursor.getCount()));
        return cursor;
    }
}


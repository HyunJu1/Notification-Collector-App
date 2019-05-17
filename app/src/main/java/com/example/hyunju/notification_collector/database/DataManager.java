package com.example.hyunju.notification_collector.database;

import android.content.ContentValues;
import android.content.Context;
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


    public void smsSending(SendedMessage sendedMessage) {
        SQLiteDatabase messageDB = messageDBHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("message_body",sendedMessage.getMessage()); // sms 에선 메세지를 body에 저장
        cv.put("create_time",sendedMessage.getTime());
        cv.put("platform",sendedMessage.PLATFORM_SMS);
        cv.put("type",sendedMessage.getType());
        cv.put("recipent_phpneNum",sendedMessage.getRecipent_phoneNum());
        messageDB.insert("message",null,cv);
        Log.d("DBDB", "성공적으로 데이터 삽입");

    }



}


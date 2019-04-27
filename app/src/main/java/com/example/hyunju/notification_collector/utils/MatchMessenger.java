package com.example.hyunju.notification_collector.utils;

import com.example.hyunju.notification_collector.models.Messenger;

import java.util.HashMap;

/**
 * 휴대폰번호로 메신저 사용여부 저장 및 반환 클래스
 */
public class MatchMessenger {

    static MatchMessenger mMatchMessenger;

    HashMap<String, Messenger> mUserInfo = new HashMap<>();

    public static MatchMessenger getInstance(){
        synchronized(MatchMessenger.class) {
            if (mMatchMessenger == null) {
                mMatchMessenger = new MatchMessenger();
            }
            return mMatchMessenger;
        }
    }

    private MatchMessenger(){
    }

    public void setUseTelegram(String phoneNum, boolean isUse){
        if (mUserInfo.get(phoneNum)!=null){
            Messenger messenger=  mUserInfo.get(phoneNum);
            messenger.telegram = isUse;
            mUserInfo.remove(phoneNum);
            mUserInfo.put(phoneNum, messenger);
        }else {
            Messenger messenger= new Messenger();
            messenger.telegram = isUse;
            mUserInfo.put(phoneNum, messenger);
        }
    }

    public void setUseEmail(String phoneNum, boolean isUse){
        if (mUserInfo.get(phoneNum)!=null){
            Messenger messenger=  mUserInfo.get(phoneNum);
            messenger.eMail = isUse;
            mUserInfo.remove(phoneNum);
            mUserInfo.put(phoneNum, messenger);
        } else {
            Messenger messenger= new Messenger();
            messenger.eMail = isUse;
            mUserInfo.put(phoneNum, messenger);
        }
    }

    public Messenger getMessengerInfo(String phoneNum){
        return mUserInfo.get(phoneNum);
    }


}

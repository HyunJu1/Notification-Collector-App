package com.example.hyunju.notification_collector.models;

import android.os.Parcel;
import android.os.Parcelable;


// Parcelable 는 인텐트에 통쨰로 담아서 전달가능
// ex) Messenger message = new Messenger()
//     intent.putExtra(message);
public class Messenger implements Parcelable {

    public boolean facebook;
    public boolean telegram;
    public boolean eMail;
    public boolean kakao;
    public boolean sms;
    public boolean mms;

    public Messenger(){
    }

    protected Messenger(Parcel in) {
        facebook = in.readByte() != 0;
        telegram = in.readByte() != 0;
        eMail = in.readByte() != 0;
        kakao = in.readByte() != 0;
        sms = in.readByte() != 0;
        mms = in.readByte() != 0;
    }

    public static final Creator<Messenger> CREATOR = new Creator<Messenger>() {
        @Override
        public Messenger createFromParcel(Parcel in) {
            return new Messenger(in);
        }

        @Override
        public Messenger[] newArray(int size) {
            return new Messenger[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (facebook ? 1 : 0));
        dest.writeByte((byte) (telegram ? 1 : 0));
        dest.writeByte((byte) (eMail ? 1 : 0));
        dest.writeByte((byte) (kakao ? 1 : 0));
        dest.writeByte((byte) (sms ? 1 : 0));
        dest.writeByte((byte) (mms ? 1 : 0));
    }
}

package com.example.hyunju.notification_collector.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SendedMessage implements Parcelable {

    public static final String PLATFORM_TELEGRAM = "telegram";
    public static final String PLATFORM_EMAIL = "email";
    public static final String PLATFORM_FACEBOOK = "facebook";
    public static final String PLATFORM_SMS = "sms";


    public String message;
    public String platfrom;

    public String time;

    public SendedMessage(){

    }

    public SendedMessage(String message, String platfrom, String time ) {
        this.message = message;
        this.platfrom = platfrom;
        this.time=time;
    }

    protected SendedMessage(Parcel in) {
        message = in.readString();
        platfrom = in.readString();
        time = in.readString();
    }

    public static final Creator<SendedMessage> CREATOR = new Creator<SendedMessage>() {
        @Override
        public SendedMessage createFromParcel(Parcel in) {
            return new SendedMessage(in);
        }

        @Override
        public SendedMessage[] newArray(int size) {
            return new SendedMessage[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPlatfrom() {
        return platfrom;
    }

    public void setPlatfrom(String platfrom) {
        this.platfrom = platfrom;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "SendedMessage{" +
                "message='" + message + '\'' +
                ", platfrom='" + platfrom + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(platfrom);
        dest.writeString(time);
    }
}

package com.example.hyunju.notification_collector.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {
    public long photoid;
    public String phonenum;
    public  String name;

    public  String email;
    public  String note;
    public  String group;
    public  String addr;

    public Contact() {
    }

    protected Contact(Parcel in) {
        photoid = in.readLong();
        phonenum = in.readString();
        name = in.readString();
        email = in.readString();
        note = in.readString();
        group = in.readString();
        addr = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(photoid);
        dest.writeString(phonenum);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(note);
        dest.writeString(group);
        dest.writeString(addr);
    }
}
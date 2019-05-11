package com.example.hyunju.notification_collector.models;

import android.graphics.Bitmap;

public class NotificationEvent {
    private String title;
    private CharSequence text;
    private CharSequence subText;
    private int smallIconRes;
    private Bitmap largeIcon;
    private String postTime;

    public NotificationEvent(String title, CharSequence text, CharSequence subText, int smallIconRes, Bitmap largeIcon, String postTime) {
        this.title = title;
        this.text = text;
        this.subText = subText;
        this.smallIconRes = smallIconRes;
        this.largeIcon = largeIcon;
        this.postTime = postTime;
    }

    public NotificationEvent(String title, CharSequence text, CharSequence subText, String postTime) {
        this.title = title;
        this.text = text;
        this.subText = subText;
        this.postTime = postTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public CharSequence getSubText() {
        return subText;
    }

    public void setSubText(CharSequence subText) {
        this.subText = subText;
    }

    public int getSmallIconRes() {
        return smallIconRes;
    }

    public void setSmallIconRes(int smallIconRes) {
        this.smallIconRes = smallIconRes;
    }

    public Bitmap getLargeIcon() {
        return largeIcon;
    }

    public void setLargeIcon(Bitmap largeIcon) {
        this.largeIcon = largeIcon;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    @Override
    public String toString() {
        return "title='" + title + '\'' +
                ", text=" + text +'\'' +
                ", subText=" + subText +
                ", postTime='" + postTime;
    }
}

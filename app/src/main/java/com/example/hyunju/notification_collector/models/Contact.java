package com.example.hyunju.notification_collector.models;

import java.io.Serializable;

public class Contact implements Serializable {
    long photoid;
    String phonenum;
    String name;

    String email;
    String note;
    String group;
    String addr;

    public Contact() {
    }

    public Contact(String phonenum, String name, String email) {
        this.phonenum = phonenum;
        this.name = name;
        this.email = email;
    }

    public long getPhotoid() {
        return photoid;
    }

    public void setPhotoid(long photoid) {
        this.photoid = photoid;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

}
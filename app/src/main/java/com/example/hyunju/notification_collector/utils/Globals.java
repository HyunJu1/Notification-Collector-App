package com.example.hyunju.notification_collector.utils;

public class Globals {
    private static Globals instance = new Globals();

    public static Globals getInstance() {
        return instance;
    }

    public static void setInstance(Globals instance) {
        Globals.instance = instance;
    }

    private String firebase_path;

    private Globals() {

    }

    public String getFirebase_path() {
        return firebase_path;
    }

    public void setFirebase_path(String firebase_path) {
        this.firebase_path = firebase_path;
    }
}

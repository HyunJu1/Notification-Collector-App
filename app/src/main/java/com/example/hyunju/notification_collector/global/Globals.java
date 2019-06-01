package com.example.hyunju.notification_collector.global;

public class Globals {
    private static Globals instance = new Globals();

    public static Globals getInstance() {
        return instance;
    }

    public static void setInstance(Globals instance) {
        Globals.instance = instance;
    }

    private String filename;

    private Globals() {

    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}

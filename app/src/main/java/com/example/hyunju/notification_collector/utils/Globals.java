package com.example.hyunju.notification_collector.utils;

public class Globals {
    private static Globals instance;

    public static Globals getInstance() {

        if(instance==null){     // Globals 인스턴스가 null 이면 초기화 실행

            /**
             * @see https://tourspace.tistory.com/54
             */
            synchronized(Globals.class){
                instance = new Globals();
            }
        }
        return instance;
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

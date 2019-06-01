package com.example.hyunju.notification_collector.global;

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

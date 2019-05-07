package com.example.hyunju.notification_collector.models;

public class ReceivedMessage {
    private String message;
    private String platfrom;

    private String time;
    public ReceivedMessage(String message, String platfrom, String time ) {
        this.message = message;
        this.platfrom = platfrom;
        this.time=time;
    }

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
        return "ReceivedMessage{" +
                "message='" + message + '\'' +
                ", platfrom='" + platfrom + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}

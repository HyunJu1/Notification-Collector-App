package com.example.hyunju.notification_collector.models;

public class SendedMessage {
    private String message;
    private String platfrom;

    public SendedMessage(String message, String platfrom) {
        this.message = message;
        this.platfrom = platfrom;
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

    @Override
    public String toString() {
        return "SendedMessage{" +
                "message='" + message + '\'' +
                ", platfrom='" + platfrom + '\'' +
                '}';
    }
}

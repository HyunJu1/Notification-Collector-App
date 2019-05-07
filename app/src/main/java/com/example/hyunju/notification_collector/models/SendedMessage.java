package com.example.hyunju.notification_collector.models;

public class SendedMessage {
    private String message;
    private String platfrom;
    private int type;
    private String time;
    /***
     * type 변수 추가 -> 0이면 send, 1이면 receive
     *
     */
    public SendedMessage(String message, String platfrom, String time, int type) {
        this.message = message;
        this.platfrom = platfrom;
        this.time = time;
        this.type = type;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
}

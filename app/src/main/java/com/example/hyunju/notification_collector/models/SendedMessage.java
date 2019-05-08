package com.example.hyunju.notification_collector.models;

import android.os.AsyncTask;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class SendedMessage {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    private String message;
    private String platfrom;
    private int type;
    private String time;

    // mail variables
    private String mailType; // 메일 본문 타입(사진, html, text 다양함)
    private Object body; // 본문

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

    /**
    * mail용
    * */
    public SendedMessage(String subject, Date date, String contentType, Object body, int type) {
        this.message = subject; // 제목
        this.platfrom = "Email";
        this.time = simpleDateFormat.format(date); // 받은 시간
        this.mailType = contentType; // 본문 타입(사진, html, text 다양함)
        this.body = body; // 본문
        this.type = type; // 타입
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

    /**
     * mail 본문 타입에 맞춰서 볼 수 있는 형태로 변환
     * **/
    public String getBody() {
        Log.e("type!!!", mailType);
        String str = "";
        if(mailType.contains("multipart/MIXED") || mailType.contains("multipart/mixed")) {
            MimeMultipart mimeMultipart = (MimeMultipart) body;
            try {
                for (int i = 0; i < mimeMultipart.getCount(); i++) {
                    BodyPart part = mimeMultipart.getBodyPart(i);
                    str = part.getContent().toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(mailType.contains("multipart")) {
            try {
                AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        String str = "";
                        Multipart multipart = (Multipart) body;
                        try {
                            int numberOfParts = multipart.getCount();
                            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                                MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(partCount);
                                str = part.getContent().toString();
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        return str;
                    }
                };
                str = asyncTask.execute().get();
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else if(mailType.contains("TEXT/PLAIN") || mailType.contains("TEXT/HTML") || mailType.contains("text/plain") || mailType.contains("text/html")) {
            if(body != null) {
                str = body.toString();
            }
        }
        return str;
    }
}

package com.example.hyunju.notification_collector.models;


import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class SendedMessage implements Parcelable {

    public static final String PLATFORM_TELEGRAM = "telegram";
    public static final String PLATFORM_EMAIL = "email";
    public static final String PLATFORM_FACEBOOK = "facebook";
    public static final String PLATFORM_SMS = "sms";


    public static final String MESSAGE_SEND = "send";

    public static final String MESSAGE_RECEIVER= "receive";

    public String message;
    public String platform;
    public String type;
    public String time;

    // mail variables
    private String mailType; // 메일 본문 타입(사진, html, text 다양함)
    private Object body; // 본문
    private String platfrom;



    public SendedMessage(String message, String platform, String time , String type) {
        this.message = message;
        this.platform = platform;
        this.time = time;
        this.type = type;
    }

    /**
    * mail용
    * */
    public SendedMessage(String subject, Date date, String contentType, Object body, String type) {
        this.message = subject; // 제목
        this.platform = "Email";
        this.time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date); // 받은 시간
        this.mailType = contentType; // 본문 타입(사진, html, text 다양함)
        this.body = body; // 본문
        this.type = type; // 타입
    }

    protected SendedMessage(Parcel in) {
        message = in.readString();
        platform = in.readString();
        time = in.readString();
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public static final Creator<SendedMessage> CREATOR = new Creator<SendedMessage>() {
        @Override
        public SendedMessage createFromParcel(Parcel in) {
            return new SendedMessage(in);
        }

        @Override
        public SendedMessage[] newArray(int size) {
            return new SendedMessage[size];
        }
    };



    @Override
    public String toString() {
        return "SendedMessage{" +
                "message='" + message + '\'' +
                ", platform='" + platform + '\'' +
                ", time='" + time + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(platform);
        dest.writeString(time);

    }

    /**
     * mail 본문 타입에 맞춰서 볼 수 있는 형태로 변환
     * **/
    public String getBody() {
        Log.e("type!!!", mailType);
        String str = "";
        if (mailType.contains("multipart/MIXED") || mailType.contains("multipart/mixed")) {
            MimeMultipart mimeMultipart = (MimeMultipart) body;
            try {
                for (int i = 0; i < mimeMultipart.getCount(); i++) {
                    BodyPart part = mimeMultipart.getBodyPart(i);
                    str = part.getContent().toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (mailType.contains("multipart")) {
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return str;
                    }
                };
                str = asyncTask.execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (mailType.contains("TEXT/PLAIN") || mailType.contains("TEXT/HTML") || mailType.contains("text/plain") || mailType.contains("text/html")) {
            if (body != null) {
                str = body.toString();
            }
        }
        return str;
    }
}

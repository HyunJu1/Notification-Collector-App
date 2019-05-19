package com.example.hyunju.notification_collector.models;


import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

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



    public String recipent_phoneNum; // sms 에서 필요해서 새로 생성
    // mail variables
    private String mailType; // 메일 본문 타입(사진, html, text 다양함)
    private Object body; // 본문
    private String body_str;
    private String platfrom;
    private ArrayList<String> attachment_str = new ArrayList<String>();
    private ArrayList<MimeBodyPart> attachment_mimebodypart = new ArrayList<MimeBodyPart>();



    public SendedMessage(String message, String platform, String time , String type) {
        this.message = message;
        this.platform = platform;
        this.time = time;
        this.type = type;
    }

    /**
     * SMS용
     */
    public SendedMessage(String message, String platform, String time , String type,String recipent_phoneNum) {
        this.message = message;
        this.platform = platform;
        this.time = time;
        this.type = type;
        this.recipent_phoneNum = recipent_phoneNum;
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

        // mail
        if(in.dataAvail() > 0) {
            mailType = in.readString();
            body_str = in.readString();
            attachment_str = in.createStringArrayList();
//            attachment_mimebodypart = in.create
        }
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
    public String getRecipent_phoneNum() {
        return recipent_phoneNum;
    }

    public void setRecipent_phoneNum(String recipent_phoneNum) {
        this.recipent_phoneNum = recipent_phoneNum;
    }

    public ArrayList<String> getAttachment_str() {
        return attachment_str;
    }

    public ArrayList<MimeBodyPart> getAttachment_mimebodypart() {
        return attachment_mimebodypart;
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

        // mail
        if(platform.equals("Email")) {
            dest.writeString(mailType);

            body_str = getBody();
            dest.writeString(body_str);
            dest.writeStringList(attachment_str);
            dest.writeList(attachment_mimebodypart);
        }
    }

    public String getBody_str() {
        return body_str;
    }

    /**
     * mail 본문 타입에 맞춰서 볼 수 있는 형태로 변환
     * **/
    public String getBody() {
        String str = "";
        Log.e("mail!!", mailType);
        if(mailType.contains("multipart")) {
            Log.e("test", "!!!!!!");
            // 이 타입은 첨부파일 없는지 확인해봐야됨 -> 있음
            if(mailType.contains("multipart/MIXED") || mailType.contains("multipart/mixed")) {
                MimeMultipart mimeMultipart = (MimeMultipart) body;
                Multipart multipart = (Multipart) body;
                try {
                    for (int i = 0; i < multipart.getCount(); i++) {
                        MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            String filename = part.getFileName();
                            attachment_str.add(MimeUtility.decodeText(filename));
                            attachment_mimebodypart.add(part);
                        }
                    }

                    for (int i = 0; i < mimeMultipart.getCount(); i++) {
                        BodyPart part = mimeMultipart.getBodyPart(i);
                        if(part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            String filename = part.getFileName();
                        }
                        str = part.getContent().toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    final AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... voids) {
                            String str = "";
                            String attachFiles = "";
                            Multipart multipart = (Multipart) body;
                            try {
                                for (int i = 0; i < multipart.getCount(); i++) {
                                    MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
                                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                                        String filename = part.getFileName();
                                        attachFiles += filename + ", ";
                                        attachment_str.add(MimeUtility.decodeText(filename));
                                        attachment_mimebodypart.add(part);
                                    }
                                    str = part.getContent().toString();
                                }

                                // 첨부파일이 1개 이상일 경우
                                if(attachFiles.length() > 1) {
                                    attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                                }

                                String value = MimeUtility.decodeText(attachFiles);

                                Log.e("test", value);
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
            }
        } else if (mailType.contains("TEXT/PLAIN") || mailType.contains("TEXT/HTML") || mailType.contains("text/plain") || mailType.contains("text/html")) {
            if (body != null) {
                str = body.toString();
            }
        }
        return str;
    }

    public void saveFile(int idx) {
        try {
            attachment_mimebodypart.get(idx).saveFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ attachment_str.get(idx));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}

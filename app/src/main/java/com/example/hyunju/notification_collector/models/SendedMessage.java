package com.example.hyunju.notification_collector.models;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

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
    public File file;



    public String recipent_phoneNum; // sms 에서 필요해서 새로 생성
    // mail variables
    private String mailType; // 메일 본문 타입(사진, html, text 다양함)
    private Object body; // 본문
    private String body_str;
    private String platfrom;
    private ArrayList<String> attachment_str = new ArrayList<String>();
    private ArrayList<MimeBodyPart> attachment_mimebodypart = new ArrayList<MimeBodyPart>();
    private ArrayList<File> attachment_file = new ArrayList<File>();
    private Object[] test = new Object[100];
    private int test_size = 0;


    public SendedMessage(){
    }

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

    public SendedMessage(String subject, String time, String body_str, ArrayList<String> attachment_str, String type) {
        this.message = subject;
        this.platform = "Email";
        this.time = time;
        this.body_str = body_str;
        this.attachment_str = attachment_str;
        this.type = type;
    }

    protected SendedMessage(Parcel in) {
        message = in.readString();
        platform = in.readString();
        time = in.readString();

        // mail
        if((in.dataAvail() > 0) && (platform.equals("Email"))) {
            mailType = in.readString();
            body_str = in.readString();
            attachment_str = in.createStringArrayList();
//            attachment_mimebodypart = in.readArrayList(MimeBodyPart.class.getClassLoader());
//            test = (Object[]) in.readArray(Object[].class.getClassLoader());
        }
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




    @Override
    public String toString() {
        return "SendedMessage{" +
                "message='" + message + '\'' +
                ", platform='" + platform + '\'' +
                ", time='" + time + '\'' +
                '}';
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
        attachment_str.clear();
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
                            Log.e("mail", MimeUtility.decodeText(filename));
//                            attachment_mimebodypart.add(part);
//                            test[test_size++] = part;

//                            part.saveFile("/data/data/com.example.hyunju.notification_collector/cache/" + filename);

//                            part.saveFile("/data/user/0/com.example.hyunju.notification_collector/cache/" + filename);

//                            part.saveFile(Environment.getDownloadCacheDirectory() + "/" + filename);
//                            part.saveFile(Environment.getExternalStorageDirectory() + "/" + filename);
//                            InputStream inputStream = part.getInputStream();
//                            FileOutputStream fileOutputStream = new FileOutputStream()

//                            DataHandler dataHandler = part.getDataHandler();
//                            String path = part.getFileName();
//                            String[] str_arr = path.split("/");
//                            String filename = MimeUtility.decodeText(str_arr[str_arr.length - 1]);
//
//                            String filePath = "/data/user/0/com.example.hyunju.notification_collector/cache/";
//                            File saveFile = new File(filePath + filename);
//                            int cnt = 0;
//                            while(saveFile.exists()) {
//                                saveFile = new File(filePath + cnt + "_" + filename);
//                            }
//
//                            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(saveFile));
//                            byte[] buff = new byte[2048];
//                            InputStream inputStream = part.getInputStream();
//                            int ret = 0;
//                            while((ret = inputStream.read(buff)) > 0) {
//                                bufferedOutputStream.write(buff, 0, ret);
//                            }
//
//                            bufferedOutputStream.close();
//                            inputStream.close();
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

                return null;
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
//                                        attachment_mimebodypart.add(part);
//                                        part.saveFile(context.getCacheDir());
//                                        test[test_size++] = part;

//                                        part.saveFile(MimeUtility.decodeText(filename));
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

//    public void saveFile(int idx) {
//        try {
//            attachment_mimebodypart.get(idx).saveFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ attachment_str.get(idx));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }

//    public File saveTempFile(Context context, String url) {
//        File file;
//        try {
//            String fileName = Uri.parse(url).getLastPathSegment();
//            file = File.createTempFile(fileName, null, context.getCacheDir());
//
//            return file;
//        } catch (Exception e) {
//
//        }
//
//        return null;
//    }
//
//    protected void saveFile(final String fileName, final InputStream in) throws IOException {
//        final File file = new File(fileName);
//        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                try {
//                    if (!file.exists()) {
//                        OutputStream out = null;
//                        out = new BufferedOutputStream(new FileOutputStream(file));
//                        InputStream in2 = new BufferedInputStream(in);
//                        byte[] buf = new byte[64 * 1024];
//                        int len;
//                        while ((len = in2.read(buf)) > 0) {
//                            out.write(buf, 0, len);
//                        }
//                        if (in2 != null) {
//                            in2.close();
//                        }
//                        if (out != null) {
//                            out.close();
//                        }
//                    } else {
//                        String e = "Fail to save file. The " + fileName + " already exists.";
//                        Log.e("error", e);
//                        throw new IOException(e);
//                    }
//                } catch (Exception e) {
//
//                }
//
//                return null;
//            }
//        };
//    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(platform);
//        dest.writeString(type);
        dest.writeString(time);
//        dest.writeString(recipent_phoneNum);
        if(platform.equals("Email")) {
            dest.writeString(mailType);
//            body_str = getBody();
            dest.writeString(body_str);
            dest.writeStringList(attachment_str);
        }

//        dest.writeInt(test_size);
    }
}

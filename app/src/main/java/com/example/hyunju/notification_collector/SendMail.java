package com.example.hyunju.notification_collector;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class SendMail extends AsyncTask<Void,Void,Void> {

    private Context context;
    private Session session;
    private String email;
    private String subject;
    private String message;
    private String filePath;
    private ProgressDialog progressDialog;

    public SendMail(Context context, String email, String subject, String message){
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    public SendMail(Context context, String email, String subject, String message, String filePath){
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.filePath = filePath;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context,"Sending message","Please wait...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
        Toast.makeText(context,"Message Sent", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Properties send_props = new Properties();

        send_props.put("mail.smtp.host", Config.SEND_HOST);
        send_props.put("mail.smtp.socketFactory.port", Config.SEND_PORT);
        send_props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        send_props.put("mail.smtp.auth", "true");
        send_props.put("mail.smtp.port", Config.SEND_PORT);

        session = Session.getInstance(send_props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Config.SEND_EMAIL, Config.SEND_PASSWORD);
                    }
                });

        try {
            MimeMessage mm = new MimeMessage(session);

            mm.setFrom(new InternetAddress(Config.SEND_EMAIL));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            mm.setSubject(subject);
            if(filePath != null) {
                BodyPart bodyPart1 = new MimeBodyPart();
                bodyPart1.setText(message);

                MimeBodyPart bodyPart2 = new MimeBodyPart();
                String filename = filePath;
                DataSource source = new FileDataSource(filename);
                bodyPart2.setDataHandler(new DataHandler(source));
                bodyPart2.setFileName(filename);

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(bodyPart1);
                multipart.addBodyPart(bodyPart2);

                mm.setContent(multipart);

            } else {

            }

            Transport.send(mm);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
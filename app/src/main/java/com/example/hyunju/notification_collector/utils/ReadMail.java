package com.example.hyunju.notification_collector.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeUtility;

import com.example.hyunju.notification_collector.configs.EmailConfig;
import com.example.hyunju.notification_collector.models.Mail;
import com.sun.mail.imap.IMAPFolder;

public class ReadMail extends AsyncTask<Integer, Void, ArrayList<Mail>> {

    public int totalMessages;
    private Session session;
    private Store store;
    private IMAPFolder folder;
    private String subject;
    private ArrayList<Mail> list;

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }


    @Override
    protected ArrayList<Mail> doInBackground(Integer... integers) {
        try {
            Properties read_props = new Properties();

            read_props.setProperty("mail.store.protocol", "imaps");

            session = Session.getDefaultInstance(read_props, null);

            store = session.getStore("imaps");
            store.connect(EmailConfig.READ_HOST, EmailConfig.READ_EMAIL, EmailConfig.READ_PASSWORD);

            folder = (IMAPFolder) store.getFolder("inbox");

            if(!folder.isOpen()) {
                folder.open(Folder.READ_WRITE);
                Message[] messages = folder.getMessages();
                list = new ArrayList<Mail>();
                totalMessages = folder.getMessageCount();
                int start_num = messages.length - 1 - Integer.parseInt(integers[0].toString());
                for(int i = start_num; i > (start_num - 10 >= 0? start_num - 10 : 0); i--) {
                    long start = System.currentTimeMillis();
                    Message msg = messages[i];

                    Log.e("email", "s : " + msg.getSubject());

                    list.add(new Mail(msg.getSubject(), MimeUtility.decodeText(msg.getFrom()[0].toString()), msg.getReceivedDate(), msg.getContentType(), msg.getContent()));
                    long end = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    protected void onPostExecute(ArrayList<Mail> list) {

    }
}
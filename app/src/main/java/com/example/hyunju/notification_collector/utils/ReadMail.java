package com.example.hyunju.notification_collector.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

import com.example.hyunju.notification_collector.configs.EmailConfig;
import com.example.hyunju.notification_collector.models.SendedMessage;
import com.sun.mail.imap.IMAPFolder;

public class ReadMail extends AsyncTask<String, Void, ArrayList<SendedMessage>> {

    public int totalMessages;
    private Session session;
    private Store store;
    private IMAPFolder folder;
    private String from;
    private String subject;
    private ArrayList<SendedMessage> list;

    private Context context;

    public ReadMail(final Context context) {
        this.context = context;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }


    @Override
    protected ArrayList<SendedMessage> doInBackground(String... strings) {
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
                list = new ArrayList<SendedMessage>();
                totalMessages = folder.getMessageCount();
                int start_num = messages.length - 1 - Integer.parseInt(strings[1]);
                for(int i = start_num; i > (start_num - 10 >= 0? start_num - 10 : 0); i--) {
                    Message msg = messages[i];
                    if(msg.getFrom()[0].toString().contains("<")) {
                        from = msg.getFrom()[0].toString().split("<")[1];
                    } else {
                        from = msg.getFrom()[0].toString().split("@")[0];
                    }

                    if(strings[0].equals(from.substring(0, from.length() - 1))) {
                        list.add(new SendedMessage(msg.getSubject(), msg.getReceivedDate(), msg.getContentType(), msg.getContent(), SendedMessage.MESSAGE_RECEIVER, context));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    protected void onPostExecute(ArrayList<SendedMessage> list) {

    }
}
package com.example.hyunju.notification_collector;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyunju.notification_collector.models.SendedMessage;

import org.w3c.dom.Text;

import java.util.ArrayList;

import javax.mail.internet.MimeBodyPart;

public class MailDetailActivity extends AppCompatActivity {

    private TextView textViewFrom;
    private TextView textViewSubject;
    private TextView textViewDate;
    private LinearLayout layoutAttachment;
    private ArrayList<String> attachment_str;
    private ArrayList<MimeBodyPart> attachment_mimebodypart;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maildetail);

        textViewFrom = (TextView) findViewById(R.id.textViewFrom);
        textViewSubject = (TextView) findViewById(R.id.textViewSubject);
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        layoutAttachment = (LinearLayout) findViewById(R.id.layout_attachment);

        webView = (WebView) findViewById(R.id.webView);

        Intent intent = getIntent();

        SendedMessage mail = (SendedMessage) intent.getParcelableExtra("mail");

//        attachment_str = intent.getStringArrayListExtra("str");

//        Log.e("attachment_str size", String.valueOf(attachment_str.size()));
//        if(attachment_str.size() > 0) {
//
//        }
//        Log.e("test", mail.type);
//        Log.e("test", mail.getMessage());
//        Log.e("test", mail.getTime());
        attachment_str = mail.getAttachment_str();
        Log.e("attachment", String.valueOf(attachment_str.size()));

        if(attachment_str != null && attachment_str.size() > 0) {
            for(int i = 0; i < attachment_str.size(); i++) {
                final TextView textViewAttachment = new TextView(MailDetailActivity.this);
                textViewAttachment.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                textViewAttachment.setPadding(20, 10, 0, 0);
                textViewAttachment.setTextColor(Color.parseColor("#7580c1"));
                textViewAttachment.setText(attachment_str.get(i));
                final int index = i;
                textViewAttachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), String.valueOf(index), Toast.LENGTH_LONG).show();
                    }
                });
                layoutAttachment.addView(textViewAttachment);
            }
        }

        textViewFrom.setText(intent.getExtras().getString("from"));
        textViewSubject.setText(mail.getMessage());

        textViewDate.setText(mail.getTime());
        webView.loadData(mail.getBody_str(), "text/html", "UTF-8");



//        textViewSubject.setText(intent.getExtras().getString("subject"));
//
//        textViewDate.setText(intent.getExtras().getString("date"));
//        webView.loadData(intent.getExtras().getString("body"), "text/html", "UTF-8");

    }
}
package com.example.hyunju.notification_collector;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.hyunju.notification_collector.models.SendedMessage;

public class MailDetailActivity extends AppCompatActivity {

    private TextView textViewFrom;
    private TextView textViewSubject;
    private TextView textViewDate;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maildetail);

        textViewFrom = (TextView) findViewById(R.id.textViewFrom);
        textViewSubject = (TextView) findViewById(R.id.textViewSubject);
        textViewDate = (TextView) findViewById(R.id.textViewDate);

        webView = (WebView) findViewById(R.id.webView);

        Intent intent = getIntent();
        Log.e("test", intent.toString());

        SendedMessage mail = (SendedMessage) intent.getParcelableExtra("mail");


//        Log.e("test", mail.type);
//        Log.e("test", mail.getMessage());
//        Log.e("test", mail.getTime());

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
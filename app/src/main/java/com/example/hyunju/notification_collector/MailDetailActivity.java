package com.example.hyunju.notification_collector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

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

        textViewFrom.setText(intent.getExtras().getString("from"));
        textViewSubject.setText(intent.getExtras().getString("subject"));

        textViewDate.setText(intent.getExtras().getString("date"));
        webView.loadData(intent.getExtras().getString("body"), "text/html", "UTF-8");

    }
}
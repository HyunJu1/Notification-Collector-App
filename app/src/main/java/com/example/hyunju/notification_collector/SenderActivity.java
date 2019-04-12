package com.example.hyunju.notification_collector;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SenderActivity extends AppCompatActivity {


    TextView textView_phone, textView_name;
    EditText editText;
    Button button;
    String phone_num, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        textView_phone = (TextView) findViewById(R.id.textView_phone_num);
        textView_name = (TextView) findViewById(R.id.textView_name);
        Intent intent = getIntent();
        phone_num = getIntent().getStringExtra("phone_num");
        name = getIntent().getStringExtra("name");

        textView_phone.setText(phone_num);
        textView_name.setText(name);

        editText = (EditText) findViewById(R.id.editText_sender);
        button = (Button) findViewById(R.id.button_sender);

        String txt = editText.getText().toString();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog();
            }
        });

    }

    public void Dialog() {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("문자");
        ListItems.add("페이스북");
        ListItems.add("텔레그램");
        ListItems.add("이메일");
        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("전송 수단을 선택하시오");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                if (pos == 0) {

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone_num, null, editText.getText().toString(), null, null);

                    Toast.makeText(SenderActivity.this, "문자 전송 성공",Toast.LENGTH_SHORT).show();
                } else {
                    String selectedText = items[pos].toString();
                    Toast.makeText(SenderActivity.this, selectedText, Toast.LENGTH_SHORT).show();
                }
            }

        });
        builder.show();
    }


}

package com.example.hyunju.notification_collector;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.SubMenu;
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
    private String phone_num, name, email;
    private Button button_attachment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        textView_phone = (TextView) findViewById(R.id.textView_phone_num);
        textView_name = (TextView) findViewById(R.id.textView_name);
        Intent intent = getIntent();
        phone_num = getIntent().getStringExtra("phone_num");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");

        textView_phone.setText(phone_num);
        textView_name.setText(name);

        editText = (EditText) findViewById(R.id.editText_sender);
        button = (Button) findViewById(R.id.button_sender);
        button_attachment = (Button) findViewById(R.id.button_attachment);

        String txt = editText.getText().toString();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog();
            }
        });

        button_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("전송 수단을 선택하시오");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                if (pos == 0) {

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone_num, null, editText.getText().toString(), null, null);

                    Toast.makeText(SenderActivity.this, "문자 전송 성공", Toast.LENGTH_SHORT).show();
                } else if(pos == 3) { // 이메일 부분.
                    Log.e("emailadsfasdfasdfasdfas", email);
                    if(email != null) { // 사용자 이메일이 저장되어있는 경우
                        AlertDialog.Builder mail_builder = new AlertDialog.Builder(SenderActivity.this); // 이메일 제목 받는 dialog창
                        mail_builder.setTitle("메일 제목을 입력해주세요");
                        final EditText editText_subject = new EditText(SenderActivity.this);
                        mail_builder.setView(editText_subject);

                        mail_builder.setPositiveButton("보내기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String subject = editText_subject.getText().toString();
                                SendMail sm = new SendMail(SenderActivity.this, email, subject, editText.getText().toString());
                                sm.execute();
                            }
                        });

                        mail_builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        mail_builder.show();
                    } else { // 사용자 이메일이 지정되어있지 않는 경우
                        Toast.makeText(SenderActivity.this, "메일주소가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    String selectedText = items[pos].toString();
                    Toast.makeText(SenderActivity.this, selectedText, Toast.LENGTH_SHORT).show();
                }
            }

        });
        builder.show();
    }


}

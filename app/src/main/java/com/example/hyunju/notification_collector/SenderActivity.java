package com.example.hyunju.notification_collector;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

public class SenderActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 6384;

    TextView textView_phone, textView_name;
    EditText editText;
    Button button;
    private String phone_num, name, email;
    private String path;
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

        button.setOnClickListener(this);
        button_attachment.setOnClickListener(this);

    }

    /**
     * 버튼 두 개라서 조잡해서 여기다가 정리함
     * **/
    @Override
    public void onClick(View v) {
        if(v == button) {
            Dialog();
        }

        if(v == button_attachment) {
            performFileSearch();
        }
    }

    public void Dialog() {
        final List<String> ListItems = new ArrayList<>();
        final List<String> MsgList = new ArrayList<>(); // ****** 여기에 메시지 저장하면 됨 *******
        ListItems.add("문자");
        ListItems.add("페이스북");
        ListItems.add("텔레그램");
        ListItems.add("이메일");
        final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("전송 수단을 선택하시오");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int pos) {
                final String text = editText.getText().toString();  // editText의 text 받아온 변수

                if (pos == 0) {

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone_num, null, text, null, null);

                    MsgList.add(text);
                    Toast.makeText(SenderActivity.this, "문자 전송 성공", Toast.LENGTH_SHORT).show();
                } else if(pos == 3) { // 이메일 부분
                    if(email != null) { // 사용자 이메일이 저장되어있는 경우
                        AlertDialog.Builder mail_builder = new AlertDialog.Builder(SenderActivity.this); // 이메일 제목 받는 dialog
                        mail_builder.setTitle("메일 제목을 입력해주세요");
                        final EditText editText_subject = new EditText(SenderActivity.this);
                        mail_builder.setView(editText_subject);

                        mail_builder.setPositiveButton("보내기", new DialogInterface.OnClickListener() { // 제목 입력 후 보내기 누른 경우
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String subject = editText_subject.getText().toString();
                                SendMail sm;
                                if(path != null) {
                                    sm = new SendMail(SenderActivity.this, email, subject, text, path);
                                } else {
                                    sm = new SendMail(SenderActivity.this, email, subject, text);
                                }
                                sm.execute();

                                MsgList.add(subject);
                            }
                        });

                        mail_builder.setNegativeButton("취소", new DialogInterface.OnClickListener() { // 취소를 누른 경우
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

    /**
     * 폰 내부의 external storage 접근하는 함수
     * **/
    private void performFileSearch() {
        Intent target = FileUtils.createGetContentIntent();
        Intent intent = Intent.createChooser(target, "Lorem ipsum");
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * 첨부파일 선택 후 해당 파일의 path 가져오는 함수
     * **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if(data != null) {
                final Uri uri = data.getData();
                path = FileUtils.getPath(this, uri);
                Log.e("path", path);
            }
        }
    }


}

package com.example.hyunju.notification_collector;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.hyunju.notification_collector.models.SendedMessage;
import com.example.hyunju.notification_collector.utils.FileUtils;
import com.example.hyunju.notification_collector.utils.ReadMail;
import com.example.hyunju.notification_collector.utils.RecyclerViewAdapter;

import com.example.hyunju.notification_collector.utils.SendFacebookMessage;
import com.example.hyunju.notification_collector.utils.SendMail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import android.app.AlertDialog;

public class ChattingActivity extends Activity implements View.OnClickListener, RecyclerViewAdapter.ItemClickListener {

    private static final int REQUEST_CODE = 6384;

    TextView textView_phone, textView_name;
    EditText editText;
    Button button;
    private String phone_num, name, email;
    private String path;
    private Button button_attachment;
    private String senderNum,message,time;
    private Context context;
    private RecyclerView rv_sendedMsg;
    private RecyclerView rv_recievdMsg;

    private RecyclerViewAdapter rv_adapter;

    private List<SendedMessage> sendedMessages;


    private String formatDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("SMS"));

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

        context = this;

        int numberOfColumns = 1;
        rv_sendedMsg = findViewById(R.id.rv_sendedMsg);
       // rv_recievdMsg = findViewById(R.id.rv_receivedMsg);

        rv_sendedMsg.setLayoutManager(new GridLayoutManager(context, numberOfColumns));
        sendedMessages = new ArrayList<SendedMessage>();

        ArrayList<SendedMessage> mails = addReceiveMail(email);
        if(mails != null) {
            for (int index = 0; index < mails.size(); index++) {
                sendedMessages.add(mails.get(index));
            }
        }

        rv_adapter = new RecyclerViewAdapter(context, sendedMessages);
        rv_adapter.setClickListener(this);
        rv_sendedMsg.setAdapter(rv_adapter);
    }

    /**
     * 해당 이메일에게 받은 메일 읽어오는 함수
     * **/
    private ArrayList<SendedMessage> addReceiveMail(String email) {
        ArrayList<SendedMessage> mails = new ArrayList<>();;

        if(email != null) {
            ReadMail rm = new ReadMail();
            try {
                mails = rm.execute(email, "10").get();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return mails;
    }

    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String senderNo = intent.getStringExtra("senderNo");
            String message = intent.getStringExtra("message");
            String receivedDate = intent.getStringExtra("receivedDate");

            try {

                SendedMessage model = new SendedMessage(message,"sms ",receivedDate,1);


                    sendedMessages.add(model);
                    rv_adapter.notifyItemChanged(sendedMessages.size() - 1);

/***
 * type 변수 추가 -> 0이면 send, 1이면 receive
 *
 */

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public String getTime() {
        // 보낸 시각 표시
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        formatDate = sdfNow.format(date);
        return formatDate;
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

                if (pos == 0) { // 문자
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone_num, null, text, null, null);

                    SendedMessage sendedMessage = new SendedMessage(text, "sms ",getTime(),0);
                    sendedMessages.add(sendedMessage);
                    rv_adapter.notifyItemChanged(sendedMessages.size() - 1);

                    Toast.makeText(ChattingActivity.this, "문자 전송 성공", Toast.LENGTH_SHORT).show();

                } else if (pos == 1) { // facebook message
                    AlertDialog.Builder recipientDialog = new AlertDialog.Builder(ChattingActivity.this);
                    recipientDialog.setTitle("수신인을 입력하세요");
                    final EditText et_recipient = new EditText(ChattingActivity.this);
                    recipientDialog.setView(et_recipient);

                    recipientDialog.setPositiveButton("보내기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String content = et_recipient.getText().toString();
                            SendFacebookMessage sfm = new SendFacebookMessage(content, text);
                            sfm.execute();

                            SendedMessage sendedMessage = new SendedMessage(text, "facebook ",getTime(),0);
                            sendedMessages.add(sendedMessage);
                            rv_adapter.notifyItemChanged(sendedMessages.size() - 1);
                        }
                    });

                    recipientDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    recipientDialog.show();
                } else if (pos == 2) { // telegram
//                    TgUtils.sendMessage(-1,"내용");

                } else if(pos == 3) { // 이메일 부분
                    if(email != null) { // 사용자 이메일이 저장되어있는 경우
                        AlertDialog.Builder mail_builder = new AlertDialog.Builder(ChattingActivity.this); // 이메일 제목 받는 dialog
                        mail_builder.setTitle("메일 제목을 입력해주세요");
                        final EditText editText_subject = new EditText(ChattingActivity.this);
                        mail_builder.setView(editText_subject);

                        mail_builder.setPositiveButton("보내기", new DialogInterface.OnClickListener() { // 제목 입력 후 보내기 누른 경우
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String subject = editText_subject.getText().toString();
                                SendMail sm;
                                if(path != null) {
                                    sm = new SendMail(ChattingActivity.this, email, subject, text, path);
                                } else {
                                    sm = new SendMail(ChattingActivity.this, email, subject, text);
                                }
                                sm.execute();

                                SendedMessage sendedMessage = new SendedMessage(text, "email ",getTime(),0);
                                sendedMessages.add(sendedMessage);
                                rv_adapter.notifyItemChanged(sendedMessages.size() - 1);
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
                        Toast.makeText(ChattingActivity.this, "메일주소가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    String selectedText = items[pos].toString();
                    Toast.makeText(ChattingActivity.this, selectedText, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemClick(View view, int position) {
        // 메일인 경우 클릭시 메일 상세페이지로 이동
        if(rv_adapter.getItem(position).getPlatfrom().equals("Email")) {
            Intent intent = new Intent(ChattingActivity.this, MailDetailActivity.class);

            intent.putExtra("subject", rv_adapter.getItem(position).getMessage());
            intent.putExtra("date", rv_adapter.getItem(position).getTime());
            intent.putExtra("body", rv_adapter.getItem(position).getBody());
            intent.putExtra("from", email);

            startActivity(intent);
        } else {
            Toast.makeText(this, rv_adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
        }
    }
}

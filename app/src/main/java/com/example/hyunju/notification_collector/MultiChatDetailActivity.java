package com.example.hyunju.notification_collector;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hyunju.notification_collector.global.GlobalApplication;
import com.example.hyunju.notification_collector.models.NotificationEvent;
import com.example.hyunju.notification_collector.models.SendedMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class MultiChatDetailActivity extends AppCompatActivity {
    private final static String TAG = MultiChatDetailActivity.class.getName();

    private TextView tv_multi_chat_detail_sended_msg;
    private ListView lv_multi_chat_detail;
    private ListViewAdapter adapter;

    private int sendedMsgIdx = 0;
    private SendedMessage sendedMessage = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_chat_detail);

        tv_multi_chat_detail_sended_msg = findViewById(R.id.tv_multi_chat_detail_sended_msg);
        Intent intent = getIntent();
        sendedMsgIdx = intent.getIntExtra("messageIdx", 0);
        sendedMessage = GlobalApplication.sendedMessageInMultiMode.get(sendedMsgIdx);
        tv_multi_chat_detail_sended_msg.setText(sendedMessage.message);

        lv_multi_chat_detail = findViewById(R.id.lv_multi_chat_detail);
        adapter = new ListViewAdapter();
        lv_multi_chat_detail.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onNotificationEvent(NotificationEvent e) {
        String phone = e.getPhone();
        ArrayList<NotificationEvent> receivedMessage = GlobalApplication.receivedMessageInMultiMode.get(sendedMsgIdx);
        for (int i = 0; i < receivedMessage.size(); i++) {
            if (receivedMessage.get(i).getPhone().equals(e.getPhone())) {
                receivedMessage.get(i).setText(e.getText());
                receivedMessage.get(i).setPostTime(e.getPostTime());
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    class ListViewAdapter extends BaseAdapter {
        LayoutInflater inflater = null;
        ArrayList<NotificationEvent> receivedMessages = GlobalApplication.receivedMessageInMultiMode.get(sendedMsgIdx);

        @Override
        public int getCount() {
            return receivedMessages.size();
        }

        @Override
        public Object getItem(int i) {
            return receivedMessages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                final Context context = viewGroup.getContext();
                if (inflater == null) {
                    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                }
                view = inflater.inflate(R.layout.item_multi_chat_detail, viewGroup, false);
            }

            TextView tv_detail_item_name = view.findViewById(R.id.tv_detail_item_name);
            tv_detail_item_name.setText(receivedMessages.get(i).getTitle());

            TextView tv_detail_item_phone = view.findViewById(R.id.tv_detail_item_phone);
            if (receivedMessages.get(i).getPhone() == null) {
                tv_detail_item_phone.setVisibility(View.GONE);
            } else {
                tv_detail_item_phone.setVisibility(View.VISIBLE);
                tv_detail_item_phone.setText(receivedMessages.get(i).getPhone());
            }

            TextView tv_detail_item_datetime = view.findViewById(R.id.tv_detail_item_datetime);
            TextView tv_detail_item_platform = view.findViewById(R.id.tv_detail_item_platform);

            TextView tv_detail_item_info_platform = view.findViewById(R.id.tv_detail_item_info_platform);
            TextView tv_detail_item_info_datetime = view.findViewById(R.id.tv_detail_item_info_datetime);
            TextView tv_detail_item_info_reply = view.findViewById(R.id.tv_detail_item_info_reply);

            if (receivedMessages.get(i).getPostTime() == null) {
                tv_detail_item_info_platform.setVisibility(View.GONE);
                tv_detail_item_info_datetime.setVisibility(View.GONE);
                tv_detail_item_info_reply.setVisibility(View.GONE);

                tv_detail_item_datetime.setVisibility(View.GONE);
                tv_detail_item_platform.setVisibility(View.GONE);
            } else {
                tv_detail_item_info_platform.setVisibility(View.VISIBLE);
                tv_detail_item_info_datetime.setVisibility(View.VISIBLE);
                tv_detail_item_info_reply.setVisibility(View.VISIBLE);

                tv_detail_item_datetime.setVisibility(View.VISIBLE);
                tv_detail_item_datetime.setText(receivedMessages.get(i).getPostTime());

                tv_detail_item_platform.setVisibility(View.VISIBLE);
                tv_detail_item_platform.setText("SMS");
            }

            TextView tv_detail_item_reply = view.findViewById(R.id.tv_detail_item_reply);
            if (receivedMessages.get(i).getText() == null) {
                tv_detail_item_reply.setVisibility(View.GONE);
            } else {
                tv_detail_item_reply.setVisibility(View.VISIBLE);
                tv_detail_item_reply.setText(receivedMessages.get(i).getText().toString());
            }

            Button btn_detail_item_request = view.findViewById(R.id.btn_detail_item_request);
            btn_detail_item_request.setVisibility(
                    receivedMessages.get(i).getPostTime() == null ? View.VISIBLE : View.GONE
            );
            if (btn_detail_item_request.getVisibility() == View.VISIBLE) {
                btn_detail_item_request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(
                                receivedMessages.get(i).getPhone(),
                                null,
                                "답장 부탁드립니다.",
                                null,
                                null
                        );
                    }
                });
            }

            return view;
        }
    }
}

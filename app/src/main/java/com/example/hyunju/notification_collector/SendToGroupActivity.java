package com.example.hyunju.notification_collector;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyunju.notification_collector.models.Contact;
import com.example.hyunju.notification_collector.models.NotificationEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class SendToGroupActivity extends AppCompatActivity {
    private final static String TAG = SendToGroupActivity.class.getName();

    private ListView lv_recipients;
    private EditText et_msg;
    private Button btn_attachment, btn_send;
    private TextView tv_group_text;

    private ListViewAdapter adapter;
    private ArrayList<Contact> contacts;
    private ArrayList<Boolean> didSend;
    private ArrayList<NotificationEvent> replyList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to_group);

        tv_group_text = findViewById(R.id.tv_group_text);
        lv_recipients = findViewById(R.id.lv_recipients);
        lv_recipients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int i, long l) {
                Contact contact = (Contact) adapter.getItemAtPosition(i);
                for (NotificationEvent e : replyList) {
                    if (e.getTitle().equals(contact.name)) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        et_msg = findViewById(R.id.et_msg);
        btn_attachment = findViewById(R.id.btn_attachment);
        btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_group_text.setText("보낸 메세지: " + et_msg.getText().toString());
                SmsManager smsManager = SmsManager.getDefault();
                for (Contact contact : contacts) {
                    smsManager.sendTextMessage(
                            contact.phonenum, null, et_msg.getText().toString(), null, null
                    );
                }
                adapter.setDidSend(true);
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), contacts.size() + "명에게 전송완료!", Toast.LENGTH_SHORT).show();
            }
        });

        contacts = (ArrayList<Contact>) getIntent().getSerializableExtra("contacts");
        didSend = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            didSend.add(false);
        }
        adapter = new ListViewAdapter(contacts, didSend);
        lv_recipients.setAdapter(adapter);
        replyList = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationEvent(NotificationEvent e) {
        String name = e.getTitle();
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).name.equals(name)) {
                didSend.set(i, true);
                adapter.notifyDataSetChanged();
                replyList.add(e);
                break;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    class ListViewAdapter extends BaseAdapter {
        LayoutInflater inflater = null;
        private ArrayList<Contact> data = null;
        private boolean didSend = false;
        private ArrayList<Boolean> didReply = null;

        public ListViewAdapter(ArrayList<Contact> data, ArrayList<Boolean> didReply) {
            this.data = data;
            this.didReply = didReply;
        }

        public void setDidSend(boolean didSend) {
            this.didSend = didSend;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                final Context context = viewGroup.getContext();
                if (inflater == null) {
                    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                }
                view = inflater.inflate(R.layout.item_listview, viewGroup, false);
            }

            TextView tv_name_item = view.findViewById(R.id.tv_name_item);
            TextView tv_phone_item = view.findViewById(R.id.tv_phone_item);
            TextView tv_dot = view.findViewById(R.id.tv_dot);
            tv_name_item.setText(data.get(i).name);
            tv_phone_item.setText(data.get(i).phonenum);
            if (!didSend) {
                tv_dot.setVisibility(View.INVISIBLE);
            } else {
                tv_dot.setVisibility(View.VISIBLE);
                tv_dot.setBackgroundColor(didReply.get(i) ? Color.GREEN : Color.RED);
            }

            return view;
        }
    }
}

package com.example.hyunju.notification_collector;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hyunju.notification_collector.global.GlobalApplication;
import com.example.hyunju.notification_collector.models.Contact;
import com.example.hyunju.notification_collector.models.SendedMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MultiChatActivity extends AppCompatActivity {
    private final static String TAG = MultiChatActivity.class.getName();

    private ListView lv_chats;
    private ListViewAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_chat);

        lv_chats = findViewById(R.id.lv_chats);
        lv_chats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, i + "!");
            }
        });

        adapter = new ListViewAdapter();
        lv_chats.setAdapter(adapter);
    }

    class ListViewAdapter extends BaseAdapter {
        LayoutInflater inflater = null;

        @Override
        public int getCount() {
            return GlobalApplication.sendedMessageInMultiMode.size();
        }

        @Override
        public Object getItem(int i) {
            return GlobalApplication.sendedMessageInMultiMode.get(i);
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
                view = inflater.inflate(R.layout.item_multi_chat, viewGroup, false);
            }

            TextView tv_multi_chat_people = view.findViewById(R.id.tv_multi_chat_people);
            tv_multi_chat_people.setText(getNames(i));

            TextView tv_multi_chat_datetime = view.findViewById(R.id.tv_multi_chat_datetime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = new Date(GlobalApplication.sendedMessageInMultiMode.get(i).time);
            String formattedDatetime = sdf.format(date);
            tv_multi_chat_datetime.setText(formattedDatetime);

            TextView tv_multi_chat_msg = view.findViewById(R.id.tv_multi_chat_msg);
            tv_multi_chat_msg.setText(GlobalApplication.sendedMessageInMultiMode.get(i).message);

            return view;
        }

        public String getNames(int idx) {
            String result = "";

            if (GlobalApplication.sendedMessageInMultiMode.size() > 0) {
                SendedMessage sendedMessage = GlobalApplication.sendedMessageInMultiMode.get(idx);
                ArrayList<Contact> contacts = sendedMessage.getRecipientContacts();
                result += contacts.get(0).name;
                result += " 외 " + (contacts.size() - 1) + "명";
            }

            return result;
        }
    }
}

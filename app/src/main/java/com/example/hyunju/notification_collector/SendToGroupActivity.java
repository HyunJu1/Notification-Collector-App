package com.example.hyunju.notification_collector;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hyunju.notification_collector.models.Contact;

import java.util.ArrayList;

public class SendToGroupActivity extends AppCompatActivity {
    private final static String TAG = SendToGroupActivity.class.getName();

    private ListView lv_recipients;
    private EditText et_msg;
    private Button btn_send;

    private ArrayList<Contact> contacts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to_group);

        lv_recipients = findViewById(R.id.lv_recipients);

        contacts = (ArrayList<Contact>) getIntent().getSerializableExtra("contacts");
        ListViewAdapter adapter = new ListViewAdapter(contacts);
        lv_recipients.setAdapter(adapter);
    }
}

class ListViewAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<Contact> data = null;

    public ListViewAdapter(ArrayList<Contact> data) {
        this.data = data;
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
        tv_name_item.setText(data.get(i).getName());
        tv_phone_item.setText(data.get(i).getPhonenum());

        return view;
    }
}

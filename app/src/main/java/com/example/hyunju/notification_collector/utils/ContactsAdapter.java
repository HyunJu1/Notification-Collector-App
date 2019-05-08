package com.example.hyunju.notification_collector.utils;


import android.app.Activity;
import com.example.hyunju.notification_collector.R;
import android.net.Uri;

import android.provider.ContactsContract;

import android.view.View;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.view.LayoutInflater;

import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hyunju.notification_collector.models.Contact;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends ArrayAdapter<Contact> {

    private int resId;
    private ArrayList<Contact> contactlist;
    private LayoutInflater Inflater;
    private Context context;

    public ContactsAdapter(Context context, int textViewResourceId,
                           List<Contact> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        resId = textViewResourceId;
        contactlist = (ArrayList<Contact>) objects;
        Inflater = (LayoutInflater) ((Activity) context)
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder;
        if (v == null) {
            v = Inflater.inflate(resId, null);
            holder = new ViewHolder();
            holder.tv_name = v.findViewById(R.id.tv_name);
            holder.tv_phonenumber = v.findViewById(R.id.tv_phonenumber);
            holder.iv_photoid = v.findViewById(R.id.iv_photo);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        Contact acontact = contactlist.get(position);

        if (acontact != null) {
            holder.tv_name.setText(acontact.getName());
            holder.tv_phonenumber.setText(acontact.getPhonenum());

            Bitmap bm = openPhoto(acontact.getPhotoid());

            if (bm != null) {
                holder.iv_photoid.setImageBitmap(bm);
            } else {
                holder.iv_photoid.setImageResource(R.drawable.user_icon);
            }

        }
        return v;
    }

    private Bitmap openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                contactId);
        InputStream input = ContactsContract.Contacts
                .openContactPhotoInputStream(context.getContentResolver(),
                        contactUri);

        if (input != null) {
            return BitmapFactory.decodeStream(input);
        }

        return null;
    }

    private class ViewHolder {
        ImageView iv_photoid;
        TextView tv_name;
        TextView tv_phonenumber;
    }

}
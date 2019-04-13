package com.example.hyunju.notification_collector;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private ListView lv_contactlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (    checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                ) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS}, 1);
        }


        lv_contactlist = (ListView) findViewById(R.id.lv_contactlist);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        // 하나라도 거부한다면.
                        new AlertDialog.Builder(this).setTitle("알림").setMessage("권한을 허용해주셔야 앱을 이용할 수 있습니다.")
                                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        MainActivity.this.finish();
                                    }
                                }).setNegativeButton("권한 설정", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                getApplicationContext().startActivity(intent);
                            }
                        }).setCancelable(false).show();

                        return;
                    }

                }

            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (    checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                ) {

            ContactsAdapter adapter = new ContactsAdapter(MainActivity.this,
                    R.layout.layout_phonelist, getContactList());

            lv_contactlist.setAdapter(adapter);
            lv_contactlist
                    .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> contactlist, View v,
                                                int position, long resid) {


                            Contact phonenumber = (Contact) contactlist.getItemAtPosition(position);

                            if (phonenumber == null) {
                                return;
                            }


                            Intent intent = new Intent(MainActivity.this, SenderActivity.class);
                            intent.putExtra("phone_num", phonenumber.getPhonenum().replaceAll("-", ""));
                            intent.putExtra("name", phonenumber.getName());

                            startActivity(intent);

                        }
                    });
        }
        else{
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS}, 1);
        }
    }


    private ArrayList<Contact> getContactList() {

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

        String[] selectionArgs = null;

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        Cursor contactCursor = managedQuery(uri, projection, null,
                selectionArgs, sortOrder);

        ArrayList<Contact> contactlist = new ArrayList<Contact>();

        if (contactCursor.moveToFirst()) {
            do {
                String phonenumber = contactCursor.getString(1).replaceAll("-",
                        "");
                if (phonenumber.length() == 10) {
                    phonenumber = phonenumber.substring(0, 3) + "-"
                            + phonenumber.substring(3, 6) + "-"
                            + phonenumber.substring(6);
                } else if (phonenumber.length() > 8) {
                    phonenumber = phonenumber.substring(0, 3) + "-"
                            + phonenumber.substring(3, 7) + "-"
                            + phonenumber.substring(7);
                }

                Contact acontact = new Contact();
                acontact.setPhotoid(contactCursor.getLong(0));
                acontact.setPhonenum(phonenumber);
                acontact.setName(contactCursor.getString(2));

                contactlist.add(acontact);
            } while (contactCursor.moveToNext());
        }

        return contactlist;

    }

    private class ContactsAdapter extends ArrayAdapter<Contact> {

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
                holder.tv_name = (TextView) v.findViewById(R.id.tv_name);
                holder.tv_phonenumber = (TextView) v
                        .findViewById(R.id.tv_phonenumber);
                holder.iv_photoid = (ImageView) v.findViewById(R.id.iv_photo);
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
                    holder.iv_photoid.setImageDrawable(getResources()
                            .getDrawable(R.mipmap.ic_launcher));
                }

            }

            return v;
        }

        private Bitmap openPhoto(long contactId) {
            Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI,
                    contactId);
            InputStream input = Contacts
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
    //
}

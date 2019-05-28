package com.example.hyunju.notification_collector;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hyunju.notification_collector.global.GlobalApplication;
import com.example.hyunju.notification_collector.models.Contact;

import com.example.hyunju.notification_collector.telegram.AuthActivity;
import com.example.hyunju.notification_collector.telegram.TgUtils;
import com.example.hyunju.notification_collector.utils.MatchMessenger;
import com.example.hyunju.notification_collector.utils.TelegramChatManager;

import com.example.hyunju.notification_collector.utils.ContactsAdapter;


import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;
import java.util.List;

//import android.app.AlertDialog;

public class MainActivity extends Activity {

    ContactsAdapter adapter;
    ArrayList<Contact> contactlist = new ArrayList<Contact>();
    private ListView lv_contactlist;
    private ImageButton btnSearch;
    private EditText edtSearch;
    private Button btn_multi, btn_multi_send, btn_settings;
    private List<Contact> list = new ArrayList<Contact>();

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, AuthActivity.class));


        lv_contactlist = findViewById(R.id.lv_contactlist);

        btnSearch = findViewById(R.id.btnSearch);
        edtSearch = findViewById(R.id.editSearch);

        btn_multi_send = findViewById(R.id.btn_multi_send);

        btn_multi = findViewById(R.id.btn_multi);
        btn_multi.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalApplication.isMultiMode = !GlobalApplication.isMultiMode;
                if (!GlobalApplication.isMultiMode) {
                    GlobalApplication.selectedContactsInMultiMode = new ArrayList<>();
                }
                btn_multi_send.setVisibility(GlobalApplication.isMultiMode ? View.VISIBLE : View.INVISIBLE);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setVisibility(View.VISIBLE);
            }
        });
        btn_multi_send.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalApplication.selectedContactsInMultiMode.size() == 0) {
                    return;
                }

                GlobalApplication.isMultiMode = false;

                Intent intent = new Intent(MainActivity.this, SendToGroupActivity.class);
                intent.putExtra("contacts", GlobalApplication.selectedContactsInMultiMode);
                startActivity(intent);
            }
        });

        if (!NotificationManagerCompat.getEnabledListenerPackages(getApplicationContext()).contains(getPackageName())) {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }

        checkPermission();
        btn_settings = findViewById(R.id.btn_settings);
        btn_settings.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });


        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Log.d("search내용",edtSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                Log.d("search내용", edtSearch.getText().toString());
                String text = edtSearch.getText().toString();
                search(text);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        btn_multi_send.setVisibility(GlobalApplication.isMultiMode ? View.VISIBLE : View.INVISIBLE);
    }

    void checkPermission() {
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED
                ) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, 1);
        } else {
            initListView();

        }
    }

    void initListView() {
        adapter = new ContactsAdapter(MainActivity.this,
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

                        if (GlobalApplication.isMultiMode) {
                            GlobalApplication.selectedContactsInMultiMode.add(phonenumber);
                            Toast.makeText(getApplicationContext(), phonenumber.name + " 추가", Toast.LENGTH_SHORT).show();
                        } else {
                           MatchMessenger.getInstance().setUseTelegram(phonenumber.phonenum,
                                   TelegramChatManager.getInstance().isChattingUser(phonenumber.phonenum));
                            Intent intent = new Intent(MainActivity.this, ChattingActivity.class);
                            intent.putExtra("contact", phonenumber);
                            startActivity(intent);
                        }

                    }
                });
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

                        break;
                    }

                }
            }

        }
    }


    // 검색을 수행하는 메소드
    public void search(String charText) {

        contactlist.clear();

        if (charText.length() == 0) {
            contactlist.addAll(list);
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).name.toLowerCase().contains(charText)) {
                    contactlist.add(list.get(i));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }


    private ArrayList<Contact> getContactList() {

        String[] arrProjection = {
                ContactsContract.Contacts._ID, // ID 열에 해당 하는 정보. 저장된 각 사용자는 고유의 ID를 가진다.
                ContactsContract.Contacts.DISPLAY_NAME // 연락처에 저장된 이름 정보.
        };

        String[] arrPhoneProjection = {
                ContactsContract.CommonDataKinds.Phone.NUMBER // 연락처에 저장된 전화번호 정보
        };

        String[] arrEmailProjection = {
                ContactsContract.CommonDataKinds.Email.DATA // 연락처에 저장된 이메일 정보
        };
        Cursor clsCursor = MainActivity.this.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                arrProjection,
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1", // HAS_PHONE_NUMBER : 하나 이상의 전화번호가 있으면 1, 그 외에는 0
                null,
                null
        );
        if (clsCursor.moveToFirst()) {
            while (clsCursor.moveToNext()) {

                String strContactId = clsCursor.getString(0);

                // Log.d("Unity", "연락처 사용자 ID : " + clsCursor.getString(0));
                Log.d("Unity", "연락처 사용자 이름 : " + clsCursor.getString(1));
                //mContact.setPhotoid(Long.parseLong(clsCursor.getString( 0 )));
                Contact contact = new Contact();
                contact.name = (clsCursor.getString(1));
                // phone number에 접근하는 Cursor
                Cursor clsPhoneCursor = MainActivity.this.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        arrPhoneProjection,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + strContactId, // where 절 : 연락처의 ID와 일치하는 전화번호를 가져온다.
                        null,
                        null
                );


                while (clsPhoneCursor.moveToNext()) {
                    //   Log.d("Unity", "연락처 사용자 번호 : " + clsPhoneCursor.getString(0));
                    contact.phonenum = (clsPhoneCursor.getString(0));
                }

                clsPhoneCursor.close();


                // email에 접근하는 Cursor
                Cursor clsEmailCursor = MainActivity.this.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        arrEmailProjection, // 연락처의 [이메일] 컬럼의 정보를 가져온다.
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + strContactId,
                        null,
                        null
                );


                while (clsEmailCursor.moveToNext()) {
                    Log.d("Unity", "연락처 사용자 email : " + clsEmailCursor.getString(0));
                    contact.email = (clsEmailCursor.getString(0));
                }

                clsEmailCursor.close();

                //텔레그램
                String phoneNum = contact.phonenum.replaceAll("-", "");
                MatchMessenger.getInstance()
                        .setUseTelegram(phoneNum, TelegramChatManager.getInstance().isChattingUser(phoneNum));

                //이메일
                MatchMessenger.getInstance()
                        .setUseEmail(phoneNum, checkEmail(contact.email));


                /**
                 *
                 *  메모 내용, 주소, 회사 정보, 직급 등의 정보 필요할 땐 아래의 코드 사용. 지금은 필요없으므로 주석처리 -> 불러오는 속도 개선
                 */
/*
>>>>>>> 1cd10fe5dd6f74bf8c15c93c1d1b27889febeb1e
                // note(메모)
                String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                String[] noteWhereParams = new String[]{
                        strContactId,
                        ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE // MIMETYPE 중 Note(즉, 메모)에 해당하는 내용을 불러오라는 뜻
                };

                // note(메모)에 접근하는 Cursor
                Cursor clsNoteCursor = MainActivity.this.getContentResolver().query(
                        ContactsContract.Data.CONTENT_URI,
                        null,
                        noteWhere,
                        noteWhereParams,
                        null
                );



                while (clsNoteCursor.moveToNext()) {

                    // Log.d("Unity", "연락처 사용자 메모 : " + clsNoteCursor.getString(clsNoteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE)));
                }
                clsNoteCursor.close();


                // address(주소지)
                String addressWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                String[] addressWhereParams = new String[]{
                        strContactId,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE  // MIMETYPE 중  StructuredPostal(즉, 우편주소)에 해당하는 내용을 불러오라는 뜻
                };

                Cursor clsAddressCursor = MainActivity.this.getContentResolver().query(
                        ContactsContract.Data.CONTENT_URI,
                        null,
                        addressWhere,
                        addressWhereParams, // addressWhere 첫번째 ?에 addressWhereParams[0]이 들어가고, 두번째 ?d에 addressWhereParams[1]이 들어간다.
                        null
                );


                while (clsAddressCursor.moveToNext()) {
//사용자 주소 쓰고싶으면 이거 활용하면 됨
//                Log.d("Unity", "연락처 사용자 주소 poBox : " + clsAddressCursor.getString(clsAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX)) );
//                Log.d("Unity", "연락처 사용자 주소 street : " + clsAddressCursor.getString(clsAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET)) );
//                Log.d("Unity", "연락처 사용자 주소 city : " + clsAddressCursor.getString(clsAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY)) );
//                Log.d("Unity", "연락처 사용자 주소 region : " + clsAddressCursor.getString(clsAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION)) );
//                Log.d("Unity", "연락처 사용자 주소 postCode : " + clsAddressCursor.getString(clsAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE)) );
//                Log.d("Unity", "연락처 사용자 주소 country : " + clsAddressCursor.getString(clsAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY)) );
//                Log.d("Unity", "연락처 사용자 주소 type : " + clsAddressCursor.getString(clsAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE)) );
                }
                // address(주소지) 정보에 접근하는 Cursor 닫는다.
                clsAddressCursor.close();


                // Organization(회사)
                String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                String[] orgWhereParams = new String[]{
                        strContactId,
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE // MIMETYPE 중  Organization(즉, 회사)에 해당하는 내용을 불러오라는 뜻
                };

                Cursor clsOrgCursor = MainActivity.this.getContentResolver().query(
                        ContactsContract.Data.CONTENT_URI,
                        null,
                        orgWhere,
                        orgWhereParams,
                        null
                );

                while (clsOrgCursor.moveToNext()) {
// 회사/ 직급 활용하고 싶으면 이거 활용
//                Log.d("Unity", "연락처 사용자 회사 : " + clsOrgCursor.getString(clsOrgCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA)));
//                Log.d("Unity", "연락처 사용자 직급 : " + clsOrgCursor.getString(clsOrgCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE)));
                }

<<<<<<< HEAD
                clsOrgCursor.close();
                contactlist.add(contact);
=======
               clsOrgCursor.close();

              */
                contactlist.add(contact);
                list.add(contact);

            }
        }
        clsCursor.close();


        return contactlist;

    }


    boolean checkEmail(String str) {
        if (str != null && !str.equals("")) {
            return str.contains("@");
        }
        return false;
    }


}
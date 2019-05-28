package com.example.hyunju.notification_collector.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.hyunju.notification_collector.R;
import com.example.hyunju.notification_collector.global.GlobalApplication;
import com.example.hyunju.notification_collector.models.ChangeGlobalStateEvent;
import com.example.hyunju.notification_collector.models.Contact;
import com.example.hyunju.notification_collector.models.SendedMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

public class GroupMessageDialogFragment extends DialogFragment {
    private static final String TAG = GroupMessageDialogFragment.class.getName();

    private TextView tv_recipient;
    private EditText et_group_message;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_multimessage, null);
        tv_recipient = view.findViewById(R.id.tv_who_received_message);
        et_group_message = view.findViewById(R.id.et_group_message);

        String recipients = "";
        for (Contact c : GlobalApplication.selectedContactsInMultiMode) {
            recipients += c.name + " ";
        }
        tv_recipient.setText(recipients);

        builder.setView(view)
                .setPositiveButton("send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SmsManager smsManager = SmsManager.getDefault();
                        String msg = et_group_message.getText().toString();
                        for (Contact c : GlobalApplication.selectedContactsInMultiMode) {
                            smsManager.sendTextMessage(
                                    c.phonenum, null, msg, null, null
                            );
                            SendedMessage message = new SendedMessage(
                                    msg,
                                    "sms",
                                    new Date(System.currentTimeMillis()).toString(),
                                    c.phonenum
                            );
                            GlobalApplication.sendedMessageInMultiMode.add(message);
                        }
                        EventBus.getDefault().post(new ChangeGlobalStateEvent(false));
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EventBus.getDefault().post(new ChangeGlobalStateEvent(false));
                        GroupMessageDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}

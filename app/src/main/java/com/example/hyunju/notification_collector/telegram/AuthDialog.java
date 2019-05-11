package com.example.hyunju.notification_collector.telegram;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hyunju.notification_collector.R;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

//사용자 인증 다이얼로그
public class AuthDialog extends AlertDialog {
    private Activity mActivity;

    private  Button mConfirm;
    private  Button mCancel;
    private EditText mEditText;
    private TextView mResendView;


    public AuthDialog(@NonNull Context context) {
        super(context);
        mActivity = (Activity) context;
        init();
    }

    void init(){
        final View view = getLayoutInflater().inflate(R.layout.dialog_login, null);
        setCancelable(false);
        setTitle("사용자인증");
        setView(view);

        mConfirm = view.findViewById(R.id.bLogin);
        mCancel = view.findViewById(R.id.btnCancel);
        mEditText = view.findViewById(R.id.ePhone);
        mResendView = view.findViewById(R.id.tvResendSMS);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TgHelper.send(new TdApi.ResetAuth(true));
                mActivity.finish();
            }
        });

        mResendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfirm.setEnabled(false);
                mEditText.setEnabled(false);
                v.setEnabled(false);
                mConfirm.setText(R.string.label_loading);

                //인증번호 다시 보내기
                TgHelper.sendOnUi(new TdApi.ResendAuthCode(), new Client.ResultHandler() {
                    @Override
                    public void onResult(TdApi.TLObject object) {
                        AuthDialog.this.dismiss();
                    }
                });
            }
        });

    }

    public Button getConfirm(){
        return mConfirm;
    }

    public Button getCancel(){
        return mCancel;
    }

    public EditText getEditText(){
        return mEditText;
    }

    public TextView getResendView(){
        return mResendView;
    }

    public void setHint(int action){
        switch (action) {
            case TdApi.AuthStateWaitPhoneNumber.CONSTRUCTOR:
                mEditText.setHint(R.string.auth_phone_hint);
                mEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case TdApi.AuthStateWaitCode.CONSTRUCTOR:
                mEditText.setHint(R.string.auth_confirmcode_hint);
                mEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case TdApi.AuthStateWaitPassword.CONSTRUCTOR:
                mEditText.setHint(R.string.auth_cloud_pass_hint);
                break;
            default:
                break;
        }
    }

    public TdApi.TLFunction getTLFunction(int action){
        // TD 서버로 보내기 위한 모델
        switch (action) {
            case TdApi.AuthStateWaitPhoneNumber.CONSTRUCTOR:
                return new TdApi.SetAuthPhoneNumber("+82"+mEditText.getText().toString(), true, true);
            case TdApi.AuthStateWaitCode.CONSTRUCTOR:
                return  new TdApi.CheckAuthCode(mEditText.getText().toString(), null, null);
            case TdApi.AuthStateWaitPassword.CONSTRUCTOR:
                return  new TdApi.CheckAuthPassword(mEditText.getText().toString());
                default:
                    return new TdApi.CheckAuthPassword("");
        }
    }

    public void messageLoading(){
        mEditText.setEnabled(false);
        mConfirm.setText(R.string.label_loading);
        mConfirm.setEnabled(false);
    }

    public void showKeyBoard(){
        mEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) (mEditText.getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.SHOW_IMPLICIT);
    }
}

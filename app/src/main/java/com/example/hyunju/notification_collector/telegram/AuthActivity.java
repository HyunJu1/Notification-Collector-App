package com.example.hyunju.notification_collector.telegram;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.hyunju.notification_collector.R;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

public class AuthActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TgHelper.init(this);
        checkAuth();
    }

    public void checkAuth(){
        TgHelper.sendOnUi(new TdApi.GetAuthState(), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                checkAuthState(object);
            }
        });

    }
    void checkAuthState(TdApi.TLObject object) {
        if (TgUtils.isError(object)) {
            TdApi.Error e = (TdApi.Error) object;
            switch (e.code) {
                case 400:
                    switch (e.message) {
                        case "PHONE_CODE_INVALID":
                            showLoginDialog(TdApi.AuthStateWaitCode.CONSTRUCTOR);
                            break;
                        case "PHONE_NUMBER_INVALID":
                            showLoginDialog(TdApi.AuthStateWaitPhoneNumber.CONSTRUCTOR);
                            break;
                        case "PASSWORD_HASH_INVALID":
                            showLoginDialog(TdApi.AuthStateWaitPassword.CONSTRUCTOR);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            return;
        }

        // instance check 해당 변수의 클래스 비교
        if (object instanceof TdApi.AuthState) {
            authStateCheck((TdApi.AuthState) object);
        }


    }
    void authStateCheck(TdApi.AuthState authState) {
        switch (authState.getConstructor()) {
            case TdApi.AuthStateWaitCode.CONSTRUCTOR:
                TdApi.AuthStateWaitCode stateWaitCode = (TdApi.AuthStateWaitCode) authState;
                boolean isRegistered = stateWaitCode.isRegistered;
                if (!isRegistered) {
                    return;
                }
                showLoginDialog(authState.getConstructor(), stateWaitCode.nextCodeType);
                break;
            case TdApi.AuthStateWaitPhoneNumber.CONSTRUCTOR:
                showLoginDialog(authState.getConstructor());
                break;
            case TdApi.AuthStateWaitPassword.CONSTRUCTOR:
                showLoginDialog(authState.getConstructor());
                break;
            case TdApi.AuthStateOk.CONSTRUCTOR:
                Toast.makeText(this, "인증완료", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }
    // dialog 띄어준다
    private void showLoginDialog(int action) {
        showLoginDialog(action, null);
    }


    private void showLoginDialog(final int action, final TdApi.AuthCodeType nextAuthCodeType) {
        final AuthDialog dialog = new AuthDialog(this);
        dialog.show();
        dialog.getEditText().setHint("휴대폰번호 입력");


        if (nextAuthCodeType == null)
            dialog.getResendView().setVisibility(View.GONE);
        else if (nextAuthCodeType.getConstructor() == TdApi.AuthCodeTypeSms.CONSTRUCTOR) {
            dialog.getResendView().setText(R.string.btnSendNewSmsCode);
            dialog.getEditText().setHint("인증번호 입력");
        } else if (nextAuthCodeType.getConstructor() == TdApi.AuthCodeTypeCall.CONSTRUCTOR) {
            dialog.getResendView().setText(R.string.btnRequestAuthCall);
        }


        dialog.getConfirm().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = dialog.getEditText().getText().toString();
                if (text.isEmpty()) {
                    return;
                }

                dialog.messageLoading();
                TgHelper.send(dialog.getTLFunction(action), new Client.ResultHandler() {
                    @Override
                    public void onResult(final TdApi.TLObject object) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                checkAuthState(object);
                            }
                        });
                    }
                });
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.showKeyBoard();
            }
        }, 500);
    }
}

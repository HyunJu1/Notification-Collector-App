package com.example.hyunju.notification_collector.global;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.hyunju.notification_collector.MainActivity;
import com.example.hyunju.notification_collector.R;
import com.example.hyunju.notification_collector.telegram.AuthDialog;
import com.example.hyunju.notification_collector.telegram.TgHelper;
import com.example.hyunju.notification_collector.telegram.TgUtils;
import com.example.hyunju.notification_collector.utils.TelegramChatManager;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.List;

public class CollectorActivity extends AppCompatActivity {


    /**
     * @param id 뷰 아이디
     * @return 뷰클래스 타입으로 반환
     */
    public <T> T view(@IdRes int id) {
        return (T) findViewById(id);
    }

    public void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 사용자인증
     */
    public void checkAuth(){
        TgHelper.sendOnUi(new TdApi.GetAuthState(), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                checkAuthState(object);
            }
        });
    }

    /**
     * 텔레그램 메신저 대화 리스트 갱신
     */
    public void refreshTelegram(){
        TelegramChatManager.getInstance().getChats(null);
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
                TelegramChatManager.getInstance().getChats(new TelegramChatManager.Callback<List<TdApi.Chat>>() {
                    @Override
                    public void onResult(List<TdApi.Chat> result) {
//                        startActivity(new Intent(CollectorActivity.this, MainActivity.class));
                        finish();
                    }
                });
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

package com.example.hyunju.notification_collector.telegram;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;


import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TG;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;

public class TgHelper {

    private static final String SETS_PROFILE_ID = "profile";
    private static ArrayList<Client.ResultHandler> list = new ArrayList<>(2);
    private static final Object LOCK = new Object();

    public static int selfProfileId;

    public static SparseArray<TdApi.User> users = new SparseArray<>();

    private static String getCacheDir(Context c) {
        return c.getCacheDir().getAbsolutePath();
    }

    public static void init(Context c) {
        TG.setFileLogEnabled(false);
        TG.setLogVerbosity(Log.WARN);
        TG.setDir(c.getFilesDir().getAbsolutePath());
        TG.setFilesDir(getCacheDir(c) + "/files/");

        startUpdatesHandler();
    }

    public static Client TG() {
        return TG.getClientInstance();
    }

    private final static Client.ResultHandler LoopUpdateHandler = new Client.ResultHandler() {
        // 다 거침
        @Override
        public void onResult(TdApi.TLObject object) {
            if (object.getConstructor() == TdApi.UpdateUser.CONSTRUCTOR) {
                updateUser((TdApi.UpdateUser) object);
            }
            synchronized (LOCK) {
                for (Client.ResultHandler r : list)
                    r.onResult(object);
            }
        }
    };

    private static void updateUser(TdApi.UpdateUser updateUser) {
        users.put(updateUser.user.id, updateUser.user);
        //TODO memory leak on long usage
        // users.put(updateUser.user.id, updateUser.user);
    }

    public static void startUpdatesHandler() {
        TG.setUpdatesHandler(LoopUpdateHandler);
    }


    // 메세지 전송
    public static void sendMessage(long chatId, String text) {
        TdApi.InputMessageText inputMessageText = new TdApi.InputMessageText();
        inputMessageText.text = text;
        TdApi.SendMessage sendMessage = new TdApi.SendMessage();
        sendMessage.chatId = chatId;
        sendMessage.inputMessageContent = inputMessageText;
        TgHelper.send(sendMessage);
    }
    //파일 전송
    public static void sendFile(long chatId, TdApi.InputFile file) {
        TdApi.InputMessageDocument inputMessageText = new TdApi.InputMessageDocument();
        inputMessageText.document = file;
        TdApi.SendMessage sendMessage = new TdApi.SendMessage();
        sendMessage.chatId = chatId;
        sendMessage.inputMessageContent = inputMessageText;
        TgHelper.send(sendMessage);
    }

    public static void send(final TdApi.TLFunction function) {
        send(function, TgUtils.emptyResultHandler());
    }


    public static void send(TdApi.TLFunction function, @Nullable final Client.ResultHandler resultHandler) {
        TG().send(function, resultHandler != null ? resultHandler : TgUtils.emptyResultHandler());
    }

    public static void sendOnUi(final TdApi.TLFunction f, final Client.ResultHandler resultHandler) {
        final Handler h = new Handler();
        send(f, new Client.ResultHandler() {
            @Override
            public void onResult(final TdApi.TLObject object) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        resultHandler.onResult(object);
                    }
                });
            }
        });
    }

    public static void getProfile(final Client.ResultHandler callback) {
        send(new TdApi.GetMe(), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                if (object.getConstructor() == TdApi.User.CONSTRUCTOR) {
                    TdApi.User me = (TdApi.User) object;
                    selfProfileId = me.id;
                    if (callback != null)
                        callback.onResult(object);
                }
            }
        });
    }


}

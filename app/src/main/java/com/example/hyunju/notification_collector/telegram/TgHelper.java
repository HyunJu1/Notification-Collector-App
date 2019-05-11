package com.example.hyunju.notification_collector.telegram;

import android.content.Context;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import com.example.hyunju.notification_collector.utils.TelegramChatManager;
import com.example.hyunju.notification_collector.utils.TelegramChatManager.MessageState;

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
        @Override
        public void onResult(TdApi.TLObject object) {
            if (object.getConstructor() == TdApi.UserStatusOffline.CONSTRUCTOR || object.getConstructor() == TdApi.UserStatusOnline.CONSTRUCTOR
                    || object.getConstructor() == TdApi.UpdateUserStatus.CONSTRUCTOR) {
                return;
            }
            if (object.getConstructor() == TdApi.UpdateUser.CONSTRUCTOR) {
                updateUser((TdApi.UpdateUser) object);
            } else if (object.getConstructor() == TdApi.UpdateNewMessage.CONSTRUCTOR) {
                TdApi.UpdateNewMessage updateNewChat = (TdApi.UpdateNewMessage) object;

                switch (sendState((updateNewChat).message)) {
                    case SUCCESS:
                        // 보냄
                        break;
                    case BEINGSENT:
                        // 보냄
                        break;
                    case INCOMING:
                        String message = ((TdApi.MessageText) updateNewChat.message.content).text;
                        // 받음
                        break;
                    default:
                        // 실패
                        break;
                }
                TdApi.Message chat = updateNewChat.message;
            } else if (object.getConstructor() == TdApi.UpdateMessageContent.CONSTRUCTOR) {
                TdApi.UpdateMessageContent updateChat = (TdApi.UpdateMessageContent) object;
                TdApi.MessageContent chat = updateChat.newContent;
            } else if (object.getConstructor() == TdApi.UpdateOption.CONSTRUCTOR) {
//                ((TdApi.UpdateOption)object).name
            } else if (object.getConstructor() == TdApi.UpdateChannel.CONSTRUCTOR) {

            }
            synchronized (LOCK) {
                for (Client.ResultHandler r : list)
                    r.onResult(object);
            }
        }
    };

    public static MessageState sendState(TdApi.Message message) {
        if (message.sendState instanceof TdApi.MessageIsIncoming) {
            return MessageState.INCOMING;
        } else if (message.sendState instanceof TdApi.MessageIsSuccessfullySent) {
            return MessageState.SUCCESS;
        } else if (message.sendState instanceof TdApi.MessageIsBeingSent) {
            return MessageState.BEINGSENT;
        }
        return MessageState.FAILED;
    }
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
    // 메세지 전송
    public static void sendMessage(long chatId, String text, TelegramChatManager.Callback callback) {
        TdApi.InputMessageText inputMessageText = new TdApi.InputMessageText();
        inputMessageText.text = text;
        TdApi.SendMessage sendMessage = new TdApi.SendMessage();
        sendMessage.chatId = chatId;
        sendMessage.inputMessageContent = inputMessageText;
        send(sendMessage, callback);
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
    public static void send(final TdApi.TLFunction function, final TelegramChatManager.Callback callback) {
        send(function, new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                callback.onResult(object);
            }
        });
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

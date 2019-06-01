package com.example.hyunju.notification_collector.telegram;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.Nullable;

import com.example.hyunju.notification_collector.models.SendedMessage;
import com.example.hyunju.notification_collector.utils.TelegramChatManager;
import com.example.hyunju.notification_collector.utils.TelegramChatManager.MessageState;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TG;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class TgHelper {

    private static ArrayList<Client.ResultHandler> list = new ArrayList<>(2);
    private static final Object LOCK = new Object();

    public static int selfProfileId;

    public static SparseArray<TdApi.User> users = new SparseArray<>();

    private static TelegramChatManager.Callback<SendedMessage> mMessageCallback;


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

    public static void setMessageCallback(TelegramChatManager.Callback<SendedMessage> callback) {
        mMessageCallback = callback;
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
                final TdApi.UpdateNewMessage updateNewChat = (TdApi.UpdateNewMessage) object;

                switch (sendState((updateNewChat).message)) {
                    case SUCCESS:
                        break;
                    case BEINGSENT:
                        // 보냄
                        break;
                    case INCOMING:
                        // 받은메시지
                        if (mMessageCallback != null) {
                            final SendedMessage sendedMessage = new SendedMessage();
                            if ((updateNewChat.message.content) instanceof TdApi.MessageText) {
                                sendedMessage.message = ((TdApi.MessageText) updateNewChat.message.content).text;
                            } else if ((updateNewChat.message.content) instanceof TdApi.MessagePhoto || (updateNewChat.message.content) instanceof TdApi.MessageDocument) {
                                sendedMessage.message = updateNewChat.message.content instanceof TdApi.MessagePhoto
                                        ? ((TdApi.MessagePhoto) updateNewChat.message.content).caption
                                        : ((TdApi.MessageDocument) updateNewChat.message.content).caption;

                                int size = ((TdApi.MessagePhoto) (updateNewChat.message.content)).photo.sizes.length;
                                TdApi.UpdateFile updateFile = new TdApi.UpdateFile(updateNewChat.message.content instanceof TdApi.MessagePhoto
                                        ? ((TdApi.MessagePhoto) updateNewChat.message.content).photo.sizes[size - 1].photo
                                        : ((TdApi.MessageDocument) updateNewChat.message.content).document.document);
                                int id = updateNewChat.message.content instanceof TdApi.MessagePhoto
                                        ? ((TdApi.MessagePhoto) updateNewChat.message.content).photo.sizes[size - 1].photo.id
                                        : ((TdApi.MessageDocument) updateNewChat.message.content).document.document.id;
                                final String persistentId = updateFile.file.persistentId;
                                sendOnUi(new TdApi.DownloadFile(id), new TelegramChatManager.Callback() {
                                    @Override
                                    public void onResult(Object result) {
                                        if (result instanceof TdApi.Ok) {
                                            sendOnUi(new TdApi.GetFilePersistent(persistentId), new TelegramChatManager.Callback() {
                                                @Override
                                                public void onResult(Object result) {
                                                    if (result instanceof TdApi.File) {
                                                        sendedMessage.file = new File(((TdApi.File) result).path);
                                                        sendedMessage.time = new Date(System.currentTimeMillis()).toString();
                                                        sendedMessage.platform = SendedMessage.PLATFORM_TELEGRAM;
                                                        sendedMessage.type = SendedMessage.MESSAGE_RECEIVER;
                                                        sendedMessage.recipent_phoneNum = TgUtils.getChatPhoneNum(updateNewChat.message.chatId);
                                                        mMessageCallback.onResult(sendedMessage);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                                return;
                            } else {
                                sendedMessage.message = "Error";
                            }
                            sendedMessage.time = new Date(System.currentTimeMillis()).toString();
                            sendedMessage.platform = SendedMessage.PLATFORM_TELEGRAM;
                            sendedMessage.type = SendedMessage.MESSAGE_RECEIVER;
                            sendedMessage.recipent_phoneNum = TgUtils.getChatPhoneNum(updateNewChat.message.chatId);
                            mMessageCallback.onResult(sendedMessage);
                        }
                        break;
                    default:
                        // 실패
                        break;
                }
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

    public static void sendOnUi(final TdApi.TLFunction f, final TelegramChatManager.Callback callback) {
        sendOnUi(f, new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                callback.onResult(object);
            }
        });
    }

    public static void sendOnUi(final TdApi.TLFunction f, final Client.ResultHandler resultHandler) {
        final Handler h = new Handler(Looper.getMainLooper());
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



//
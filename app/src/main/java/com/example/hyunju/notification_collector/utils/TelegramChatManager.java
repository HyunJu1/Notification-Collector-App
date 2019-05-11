package com.example.hyunju.notification_collector.utils;


import android.telecom.Call;

import com.example.hyunju.notification_collector.telegram.TgHelper;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TelegramChatManager {

    public static final long EXTRA_EMPTY_CHAT_ID= -404;

    private HashMap<String, TdApi.Chat> mChatList = new HashMap<>();
    private static TelegramChatManager mTelegramChatManager;

    public static TelegramChatManager getInstance() {
        if (mTelegramChatManager == null) {
            synchronized (TelegramChatManager.class) {
                if (mTelegramChatManager == null) {
                    mTelegramChatManager = new TelegramChatManager();
                }
            }
        }
        return mTelegramChatManager;
    }

    private TelegramChatManager() {
    }

    /**
     * 채팅방 목록 가져오는 메소드
     * @param callback 콜백
     */
    public void getChats(final Callback<List<TdApi.Chat>> callback) {
        TdApi.GetChats getChats = new TdApi.GetChats(Long.MAX_VALUE, 0, 200); //temporarily,
        TgHelper.send(getChats, new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                List<TdApi.Chat> result = new ArrayList<>();
                for (TdApi.Chat chat : ((TdApi.Chats) object).chats) {
                    if (getPhoneNumber(chat) != null) {
//                        LastChatMessage.getInstance().setLastMessageId(chat.id, chat);
                        mChatList.put(getPhoneNumber(chat), chat);      // 폰번호나 대화방이름으로 저장
                        result.add(chat);
                    }
                }
                if (callback != null) {
                    callback.onResult(result);
                }
            }
        });
    }

    String getPhoneNumber(TdApi.Chat chat) {
        String chatName = chat.title;
        if (chat.type instanceof TdApi.PrivateChatInfo) {
            String phoneNumber = (((TdApi.PrivateChatInfo) chat.type).user).phoneNumber;

            return phoneNumber.equals("") && chatName.equals("")    // 탈퇴한자의 온기만 남은 대화방
                    ? null : chatName.equals("Telegram")
                    ? null : phoneNumber.equals("")
                    ? chatName : phoneNumber.startsWith("82")
                    ? phoneNumber.replaceFirst("82", "0") : phoneNumber;
        }
        return chat.title;
    }

    /**
     * @param phoneNumber
     * @return 해당 채팅방의 마지막메시지
     */
    public String getLastMessage(String phoneNumber) {
        return ((TdApi.MessageText) mChatList.get(phoneNumber).topMessage.content).text;
    }

    /**
     * @param phoneNumber
     * @return 해당 채팅방 마지막 메시지의 전송상태
     */
    public MessageState getLastSendMessageState(String phoneNumber) {
        TdApi.MessageSendState state = (mChatList.get(phoneNumber).topMessage).sendState;
        if(state instanceof TdApi.MessageIsBeingSent){
            return MessageState.BEINGSENT;
        } else if(state instanceof TdApi.MessageIsSuccessfullySent){
            return MessageState.SUCCESS;
        } else if(state instanceof TdApi.MessageIsIncoming){
            return MessageState.INCOMING;
        } else {
            return MessageState.FAILED;
        }
    }

    public boolean isChattingUser(String phoneNum){
        return mChatList.get(phoneNum) != null;
    }

    public void sendMessage(long chatId, String text, Callback callback) {
        TdApi.InputMessageText inputMessageText = new TdApi.InputMessageText();
        inputMessageText.text = text;
        TdApi.SendMessage sendMessage = new TdApi.SendMessage();
        sendMessage.chatId = chatId;
        sendMessage.inputMessageContent = inputMessageText;
        TgHelper.send(sendMessage, callback);
    }
    public void sendFile(long chatId, String text,  TdApi.InputFile file, Callback callback) {
        TdApi.InputMessageDocument inputMessageDocument = new TdApi.InputMessageDocument();
        inputMessageDocument.caption = text;
        inputMessageDocument.document = file;
        TdApi.SendMessage sendMessage = new TdApi.SendMessage();
        sendMessage.chatId = chatId;
        sendMessage.inputMessageContent = inputMessageDocument;
        TgHelper.send(sendMessage,callback);
    }

    public HashMap<String, TdApi.Chat> getChatList() {
        return mChatList;
    }

    public long getChatId(String phoneNum){
        if(mChatList.get(phoneNum)!=null) {
            return mChatList.get(phoneNum).id;
        }
        return EXTRA_EMPTY_CHAT_ID;
    }

    public interface Callback<T> {
        void onResult(T result);
    }


    /**
     * 메시지 전송 상태
     */

    public enum MessageState {
        /**
         * 메시지 전송은 했으나 아직 텔레그램 서버는 못받은 상태
         */
        BEINGSENT,

        /**
         * 메시지 보낸게 서버에 동기화완료
         */
        SUCCESS,

        /**
         * 받은메시지
         */
        INCOMING,

        /**
         * 전송실패
         */
        FAILED
    }

}



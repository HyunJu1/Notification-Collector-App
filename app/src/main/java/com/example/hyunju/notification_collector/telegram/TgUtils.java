package com.example.hyunju.notification_collector.telegram;


import com.example.hyunju.notification_collector.utils.TelegramChatManager;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.Client.ResultHandler;
import org.drinkless.td.libcore.telegram.TdApi;

public class TgUtils {

    public final static ResultHandler emptyResultHandler() {
        return new ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {

            }
        };
    }

    public static boolean isError(final TdApi.TLObject object) {
        return object.getConstructor() == TdApi.Error.CONSTRUCTOR;
    }

    public static boolean isSuperGroup(final int chatType) {
        return chatType == TdApi.ChannelChatInfo.CONSTRUCTOR;
    }

    // 채팅방 정보 가져올떼
    public static void getChatFullInfo(TdApi.Chat chat, final ResultHandler callbackHandler) {
        if (TgUtils.isSuperGroup(chat.type.getConstructor())) {
            TdApi.ChannelChatInfo ci = (TdApi.ChannelChatInfo) chat.type;
            TgHelper.send(new TdApi.GetChannelFull(ci.channel.id), new Client.ResultHandler() {
                @Override
                public void onResult(TdApi.TLObject object) {
                    callbackHandler.onResult(object);
                }
            });
        } else {
            TdApi.GroupChatInfo gi = (TdApi.GroupChatInfo) chat.type;
            TgHelper.send(new TdApi.GetGroupFull(gi.group.id), new ResultHandler() {
                @Override
                public void onResult(TdApi.TLObject object) {
                    callbackHandler.onResult(object);
                }
            });
        }

    }


    // 이름으로 텔레그램 연락처 검색
    public static void getTelegramContact(String name, final TelegramChatManager.Callback<TdApi.Users> callback) {
        TgHelper.send(new TdApi.SearchContacts(name, 200), new Client.ResultHandler() {
            @Override
            public void onResult(TdApi.TLObject object) {
                callback.onResult((TdApi.Users) object);
            }
        });
    }

    public static String getChatPhoneNum(long userId){
        TdApi.User user = TgHelper.users.get((int)userId);
        if(user!=null){
            return user.phoneNumber.replaceFirst("82","0");
        }
        return "";
    }

    // 텔래그렘 사용자 정보
    public static TdApi.User getUser(int userId) {
        TdApi.User user = TgHelper.users.get(userId);
        if(user==null){
            user = new TdApi.User();
            user.id = userId;
            user.firstName=user.lastName="";
            user.username="";
        }
        return user;
    }

    //텔레그램 사용자 정보
    public static TdApi.User getUser(TdApi.ChatMember member) {
        return getUser(member.userId);
    }

    // 채팅방 채팅상대 이름
    public static String getChatUsername(TdApi.Chat chat) {
        if(isSuperGroup(chat.type.getConstructor())){
            TdApi.ChannelChatInfo channelInfo = (TdApi.ChannelChatInfo) chat.type;
            return channelInfo.channel.username;
        }
        return null;
    }
}
//
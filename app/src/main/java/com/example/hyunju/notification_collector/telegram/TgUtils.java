package com.example.hyunju.notification_collector.telegram;


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

    public static TdApi.User getUser(TdApi.ChatMember member) {
        return getUser(member.userId);
    }

    public static String getChatUsername(TdApi.Chat chat) {
        if(isSuperGroup(chat.type.getConstructor())){
            TdApi.ChannelChatInfo channelInfo = (TdApi.ChannelChatInfo) chat.type;
            return channelInfo.channel.username;
        }
        return null;
    }
}

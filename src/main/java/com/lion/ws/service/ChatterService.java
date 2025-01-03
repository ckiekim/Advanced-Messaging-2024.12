package com.lion.ws.service;

import com.lion.ws.entity.*;
import com.lion.ws.util.ChatUtil;
import com.lion.ws.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatterService {
    @Autowired private ChatMessageService chatMessageService;
    @Autowired private ChatUserService chatUserService;
    @Autowired private ChatUtil chatUtil;
    @Autowired private TimeUtil timeUtil;

    public List<Chatter> getChatterList(User user) {
        String uid = user.getUid();
        List<ChatUser> chatUsers = chatUserService.findByUserUid(uid);
        List<Chatter> chatterList = new ArrayList<>();
        for (ChatUser chatUser: chatUsers) {
            ChatRoom chatRoom = chatUser.getChatRoom();
            long chatRoomId = chatRoom.getId();
            String roomName = chatUtil.getRoomName(uid, chatRoom);
            String profile = chatUtil.getProfileUrl(uid, chatRoom);
            int newCount = chatMessageService.getNewCount(chatRoomId, user);

            ChatMessage chatMessage = chatMessageService.getLastMessage(chatRoomId);
            Chatter chatter = Chatter.builder()
                    .roomId(chatRoomId)
                    .roomName(roomName)
                    .roomProfile(profile)
                    .message(chatMessage != null ? chatMessage.getMessage() : "")
                    .timeStr(chatMessage != null ? timeUtil.timeAgo(chatMessage.getTimestamp()) : "")
                    .newCount(newCount)
                    .build();
            chatterList.add(chatter);
        }
        return chatterList;
    }
}

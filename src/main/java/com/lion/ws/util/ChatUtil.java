package com.lion.ws.util;

import com.lion.ws.entity.ChatRoom;
import com.lion.ws.entity.ChatUser;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ChatUtil {

    public String getRoomName(String uid, ChatRoom chatRoom) {
        String roomName = chatRoom.getName();
        if (chatRoom.getMembers().size() == 2) {
            for (ChatUser member: chatRoom.getMembers()) {
                if (member.getUser().getUid().equals(uid))
                    continue;
                if (roomName.equals("personal"))
                    roomName = member.getUser().getUname();
            }
        } else {
            if (roomName.equals("group")) {
                roomName = chatRoom.getMembers().stream()
                        .map(cu -> cu.getUser().getUname())
                        .collect(Collectors.joining(", "));
                if (roomName.length() > 20) {
                    int roomSize = chatRoom.getMembers().size();
                    roomName = roomName.substring(0, 18) + "... " + roomSize;
                }
            }
        }
        return roomName;
    }

    public String getProfileUrl(String uid, ChatRoom chatRoom) {
        String profile = "/img/people.png";
        if (chatRoom.getMembers().size() == 2) {    // 1 : 1 채팅에서는 상대방의 Profile URL
            for (ChatUser member: chatRoom.getMembers()) {
                if (!member.getUser().getUid().equals(uid))
                    return member.getUser().getProfileUrl();
            }
        }
        return profile;
    }
}

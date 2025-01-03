package com.lion.ws.service;

import com.lion.ws.entity.ChatItem;
import com.lion.ws.entity.ChatMessage;
import com.lion.ws.entity.User;
import com.lion.ws.repository.ChatMessageRepository;
import com.lion.ws.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ChatMessageService {
    @Autowired private ChatMessageRepository chatMessageRepository;
    @Autowired private TimeUtil timeUtil;

    public ChatMessage findById(long roomId) {
        return chatMessageRepository.findById(roomId).orElse(null);
    }

    public List<ChatMessage> findByChatRoomId(long roomId) {
        return chatMessageRepository.findByChatRoomId(roomId);
    }

    public ChatMessage getLastMessage(long roomId) {
        List<ChatMessage> cmList = chatMessageRepository.findByChatRoomIdOrderByTimestampDesc(roomId);
        return cmList.size() >= 1 ? cmList.get(0) : null;
    }

    public int getNewCount(long roomId, User user) {
        List<ChatMessage> cmList = chatMessageRepository.findByChatRoomId(roomId);
        int count = 0;
        for (ChatMessage chatMessage: cmList) {
            Set<User> set = chatMessage.getReadUsers();
            if (!set.contains(user))
                count++;
        }
        return count;
    }

    public void insertChatMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }

    public void updateUnreadCount(ChatMessage chatMessage, User user) {
        Set<User> set = chatMessage.getReadUsers();
        if (!set.contains(user)) {
            set.add(user);
            chatMessage.setReadUsers(set);
            chatMessage.setUnreadCount(chatMessage.getUnreadCount() - 1);
            chatMessageRepository.save(chatMessage);
        }
    }

    public Map<String, List<ChatMessage>> getChatMessagesByDate(long roomId) {
        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomId(roomId);
        Map<String, List<ChatMessage>> map = new LinkedHashMap<>();
        for (ChatMessage cm: chatMessageList) {
            String date = cm.getTimestamp().toString().substring(0, 10);
            if (map.containsKey(date)) {
                List<ChatMessage> cmList = map.get(date);
                cmList.add(cm);
                map.replace(date, cmList);
            } else {
                List<ChatMessage> cmList = new ArrayList<>();
                cmList.add(cm);
                map.put(date, cmList);
            }
        }
        return map;
    }

    public Map<String, List<ChatItem>> getChatItemsByDate(long roomId, User user) {
        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatRoomId(roomId);
        Map<String, List<ChatItem>> map = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd (E)", Locale.KOREAN);
        for (ChatMessage chatMessage: chatMessageList) {
            String key = chatMessage.getTimestamp().toString().substring(0, 10);
            String date = LocalDate.parse(key).format(formatter);
            updateUnreadCount(chatMessage, user);
            if (map.containsKey(date)) {
                List<ChatItem> ciList = map.get(date);
                ChatItem chatItem = genChatItem(chatMessage, user.getUid());
                ciList.add(chatItem);
                map.replace(date, ciList);
            } else {
                List<ChatItem> ciList = new ArrayList<>();
                ChatItem chatItem = genChatItem(chatMessage, user.getUid());
                ciList.add(chatItem);
                map.put(date, ciList);
            }
        }
        return map;
    }

    private ChatItem genChatItem(ChatMessage cm, String uid) {
        ChatItem chatItem = ChatItem.builder()
                .isMine(cm.getSender().getUid().equals(uid) ? 1 : 0)
                .message(cm.getMessage())
                .timeStr(timeUtil.amPmStr(cm.getTimestamp()))
                .senderName(cm.getSender().getUname())
                .senderProfile(cm.getSender().getProfileUrl())
                .unreadCount(cm.getUnreadCount())
                .build();
        return chatItem;
    }
}

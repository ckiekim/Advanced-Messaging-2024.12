package com.lion.ws.service;

import com.lion.ws.entity.ChatMessage;
import com.lion.ws.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatMessageService {
    @Autowired private ChatMessageRepository chatMessageRepository;

    public ChatMessage findById(long id) {
        return chatMessageRepository.findById(id).orElse(null);
    }

    public List<ChatMessage> findByChatRoomId(long id) {
        return chatMessageRepository.findByChatRoomId(id);
    }

    public ChatMessage getLastMessage(long id) {
        return chatMessageRepository.findByChatRoomIdOrderByTimestampDesc(id).get(0);
    }

    public void insertChatMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
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
}

package com.lion.ws.service;

import com.lion.ws.entity.ChatMessage;
import com.lion.ws.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}

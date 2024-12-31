package com.lion.ws.service;

import com.lion.ws.entity.ChatRoom;
import com.lion.ws.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatRoomService {
    @Autowired private ChatRoomRepository chatRoomRepository;

    public ChatRoom findById(long id) {
        return chatRoomRepository.findById(id).orElse(null);
    }

    public void insertChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
    }
}

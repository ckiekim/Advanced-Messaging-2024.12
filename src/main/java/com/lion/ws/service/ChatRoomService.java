package com.lion.ws.service;

import com.lion.ws.entity.ChatRoom;
import com.lion.ws.repository.ChatRoomRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChatRoomService {
    @Autowired private ChatRoomRepository chatRoomRepository;

    public ChatRoom findById(long id) {
        return chatRoomRepository.findById(id).orElse(null);
    }

    public ChatRoom findByIdWithMembers(long id) {
        return chatRoomRepository.findByIdWithMembers(id)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found"));
    }

    public void insertChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
    }

    public void updateChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
    }
}

package com.lion.ws.service;

import com.lion.ws.entity.ChatUser;
import com.lion.ws.repository.ChatUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatUserService {
    @Autowired private ChatUserRepository chatUserRepository;

    public ChatUser findById(long id) {
        return chatUserRepository.findById(id).orElse(null);
    }

    public List<ChatUser> findByUserUid(String uid) {
        return chatUserRepository.findByUserUid(uid);
    }

    public void insertChatUser(ChatUser chatUser) {
        chatUserRepository.save(chatUser);
    }
}

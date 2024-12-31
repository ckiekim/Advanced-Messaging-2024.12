package com.lion.ws.repository;

import com.lion.ws.entity.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {
    List<ChatUser> findByUserUid(String uid);
}

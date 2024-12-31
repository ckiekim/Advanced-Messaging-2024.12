package com.lion.ws.repository;

import com.lion.ws.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomId(long id);

    List<ChatMessage> findByChatRoomIdOrderByTimestampDesc(long id);
}

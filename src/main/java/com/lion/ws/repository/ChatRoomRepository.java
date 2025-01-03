package com.lion.ws.repository;

import com.lion.ws.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT c FROM ChatRoom c LEFT JOIN FETCH c.members WHERE c.id = :id")
    Optional<ChatRoom> findByIdWithMembers(@Param("id") Long id);
}

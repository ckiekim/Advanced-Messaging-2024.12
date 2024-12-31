package com.lion.ws.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ChatUserTest {
    private ChatRoom chatRoom;
    private User user;

    @BeforeEach
    public void setup() {
        chatRoom = ChatRoom.builder()
                .id(1L).name("test room").timestamp(LocalDateTime.now())
                .build();
        user = User.builder()
                .uid("test").uname("John Doe").email("john.doe@example.com")
                .build();
    }

    @Test
    public void testChatUserSet() {
        Set<ChatUser> chatUsers = new HashSet<>();

        ChatUser chatUser1 = ChatUser.builder()
                .id(1L).chatRoom(chatRoom).user(user).joinedAt(LocalDateTime.now())
                .build();
        ChatUser chatUser2 = ChatUser.builder()
                .id(2L).chatRoom(chatRoom).user(user).joinedAt(LocalDateTime.now())
                .build();

        chatUsers.add(chatUser1);
        chatUsers.add(chatUser2);

        assertThat(chatUsers).hasSize(1);
    }
}

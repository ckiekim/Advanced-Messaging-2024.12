package com.lion.ws.controller;

import com.lion.ws.entity.ChatRoom;
import com.lion.ws.entity.ChatUser;
import com.lion.ws.entity.User;
import com.lion.ws.service.ChatRoomService;
import com.lion.ws.service.ChatUserService;
import com.lion.ws.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/chat")
public class ChatController {
    @Autowired private ChatRoomService chatRoomService;
    @Autowired private ChatUserService chatUserService;
    @Autowired private UserService userService;

    @GetMapping("/mock")
    public String mockForm() {
        return "chat/mock";
    }

    @PostMapping("/mock")
    public String mockProc(String ownerName, String roomName, String nameStr) {
        User owner = userService.findByUid(ownerName.strip());
        ChatRoom chatRoom = ChatRoom.builder()
                .name(roomName)
                .owner(owner)
                .timestamp(LocalDateTime.now())
                .build();

        String[] names = nameStr.split(",");
        Set<ChatUser> members = new HashSet<>();
        for (String name: names) {
            User user = userService.findByUid(name.strip());
            ChatUser chatUser = ChatUser.builder()
                    .chatRoom(chatRoom)
                    .user(user)
                    .joinedAt(LocalDateTime.now())
                    .build();
            members.add(chatUser);
        }
        chatRoom.setMembers(members);
        chatRoomService.insertChatRoom(chatRoom);
        return "redirect:/chat/mock";
    }
}

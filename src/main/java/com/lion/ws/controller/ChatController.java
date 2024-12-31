package com.lion.ws.controller;

import com.lion.ws.entity.*;
import com.lion.ws.service.ChatMessageService;
import com.lion.ws.service.ChatRoomService;
import com.lion.ws.service.ChatUserService;
import com.lion.ws.service.UserService;
import com.lion.ws.util.TimeUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/chat")
public class ChatController {
    @Autowired private ChatMessageService chatMessageService;
    @Autowired private ChatRoomService chatRoomService;
    @Autowired private ChatUserService chatUserService;
    @Autowired private UserService userService;
    @Autowired private TimeUtil timeUtil;


    @GetMapping("/initRoom")
    public String initRoomForm() {
        return "chat/initRoom";
    }

    @PostMapping("/initRoom")
    public String initRoomProc(String ownerName, String roomName, String nameStr) {
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
        return "redirect:/chat/initRoom";
    }

    @GetMapping("/list/{uid}")
    @ResponseBody
    public String listByUser(@PathVariable String uid) {
        List<ChatUser> chatUsers = chatUserService.findByUserUid(uid);
        String html = "";
        for (ChatUser cu: chatUsers)
            html += cu.toString() + "<br>";
        return html;
    }

    @GetMapping("/initMessage")
    public String initMessageForm() {
        return "chat/initMessage";
    }

    @PostMapping("/initMessage")
    public String initMessageProc(String senderName, long roomId, String message) {
        User sender = userService.findByUid(senderName.strip());
        ChatRoom chatRoom = chatRoomService.findById(roomId);
        int unreadCount = chatRoom.getMembers().size() - 1;
        Set<User> readUsers = new HashSet<>();
        readUsers.add(sender);
        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sender)
                .chatRoom(chatRoom)
                .message(message)
                .timestamp(LocalDateTime.now())
                .unreadCount(unreadCount)
                .readUsers(readUsers)
                .build();
        chatMessageService.insertChatMessage(chatMessage);
        return "redirect:/chat/initMessage";
    }

    @GetMapping("/chatter/{uid}")
    public String messageByUser(@PathVariable String uid, Model model) {
        List<ChatUser> chatUsers = chatUserService.findByUserUid(uid);
        List<Chatter> chatterList = new ArrayList<>();
        for (ChatUser chatUser: chatUsers) {
            ChatRoom chatRoom = chatUser.getChatRoom();
            long chatRoomId = chatRoom.getId();
            String profile = "/img/people.png";
            String roomName = "";
            if (chatRoom.getMembers().size() == 2) {
                for (ChatUser member: chatRoom.getMembers()) {
                    if (member.getUser().getUid().equals(uid))
                        continue;
                    profile = member.getUser().getProfileUrl();
                    roomName = member.getUser().getUname();
                }
            } else {
                roomName = chatRoom.getMembers().stream()
                        .map(cu -> cu.getUser().getUname())
                        .collect(Collectors.joining(", "));
            }
            ChatMessage chatMessage = chatMessageService.getLastMessage(chatRoomId);
            Chatter chatter = Chatter.builder()
                    .roomId(chatRoomId)
                    .roomName(roomName)       // 추후 수정
                    .roomProfile(profile)
                    .message(chatMessage.getMessage())
                    .timeStr(timeUtil.timeAgo(chatMessage.getTimestamp()))
                    .unreadCount(chatMessage.getUnreadCount())
                    .build();
            chatterList.add(chatter);
        }
        model.addAttribute("user", userService.findByUid(uid));
        model.addAttribute("chatterList", chatterList);
        return "chat/chatter";
    }
}

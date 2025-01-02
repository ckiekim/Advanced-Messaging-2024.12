package com.lion.ws.controller;

import com.lion.ws.entity.*;
import com.lion.ws.service.*;
import com.lion.ws.util.ChatUtil;
import com.lion.ws.util.TimeUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/chat")
public class ChatController {
    @Autowired private ChatMessageService chatMessageService;
    @Autowired private ChatRoomService chatRoomService;
    @Autowired private ChatUserService chatUserService;
    @Autowired private ChatterService chatterService;
    @Autowired private UserService userService;
    @Autowired private ChatUtil chatUtil;
    @Autowired private TimeUtil timeUtil;

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        String uid = (String) session.getAttribute("sessUid");
        User user = userService.findByUid(uid);
        List<Chatter> chatterList = chatterService.getChatterList(uid);

        model.addAttribute("chatterList", chatterList);
        model.addAttribute("user", user);
        return "chat/home";
    }

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
        List<Chatter> chatterList = chatterService.getChatterList(uid);
        model.addAttribute("user", userService.findByUid(uid));
        model.addAttribute("chatterList", chatterList);
        return "chat/chatter";
    }

    @GetMapping("/room/{roomId}")
    public String messagesByRoom(@PathVariable long roomId, HttpSession session, Model model) {
        String uid = (String) session.getAttribute("sessUid");
        ChatRoom chatRoom = chatRoomService.findById(roomId);
        String roomName = chatUtil.getRoomName(uid, chatRoom);
        String roomProfile = chatUtil.getProfileUrl(uid, chatRoom);
        Map<String, List<ChatMessage>> map = chatMessageService.getChatMessagesByDate(roomId);

        Map<String, List<ChatItem>> chatItemsByDate = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd (E)", Locale.KOREAN);
        for (String key: map.keySet()) {
            List<ChatItem> ciList = new ArrayList<>();
            for (ChatMessage cm: map.get(key)) {
                ChatItem chatItem = ChatItem.builder()
                        .isMine(cm.getSender().getUid().equals(uid) ? 1 : 0)
                        .message(cm.getMessage())
                        .timeStr(timeUtil.amPmStr(cm.getTimestamp()))
                        .senderName(cm.getSender().getUname())
                        .senderProfile(cm.getSender().getProfileUrl())
                        .unreadCount(cm.getUnreadCount())
                        .build();
                ciList.add(chatItem);
            }
            String date = LocalDate.parse(key).format(formatter);
            chatItemsByDate.put(date, ciList);
        }

        model.addAttribute("roomName", roomName);
        model.addAttribute("roomProfile", roomProfile);
        model.addAttribute("chatItemsByDate", chatItemsByDate);
        return "chat/room";
    }
}
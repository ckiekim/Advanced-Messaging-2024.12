package com.lion.ws.controller;

import com.lion.ws.entity.*;
import com.lion.ws.service.*;
import com.lion.ws.util.ChatUtil;
import com.lion.ws.util.TimeUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
    @Value("${server.port}") private String serverPort;
    @Value("${server.ip}") private String serverIp;

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        String uid = (String) session.getAttribute("sessUid");
        User user = userService.findByUid(uid);
        List<Chatter> chatterList = chatterService.getChatterList(user);
        String roomListStr = chatterList.stream()
                .map(chatter -> String.valueOf(chatter.getRoomId()))
                .collect(Collectors.joining(","));

        session.setAttribute("serverPort", serverPort);
        session.setAttribute("serverIp", serverIp);
        session.setAttribute("menu", "chat");
        model.addAttribute("roomListStr", roomListStr);
        model.addAttribute("user", user);
        return "chat/home";
    }

    @GetMapping("/getChatterList")
    @ResponseBody
    public ResponseEntity<List<Chatter>> getChatterList(@RequestParam String userId) {
        User user = userService.findByUid(userId);
        List<Chatter> chatterList = chatterService.getChatterList(user);
        return ResponseEntity.ok(chatterList);
    }

    @GetMapping("/each/{roomId}")
    public String each(@PathVariable long roomId, HttpSession session, Model model) {
        String uid = (String) session.getAttribute("sessUid");
        User user = userService.findByUid(uid);
        ChatRoom chatRoom = chatRoomService.findById(roomId);
        String roomName = chatUtil.getRoomName(uid, chatRoom);
//        Map<String, List<ChatItem>> chatItemsByDate = chatMessageService.getChatItemsByDate(roomId, user);

        model.addAttribute("user", user);
        model.addAttribute("roomId", roomId);
        model.addAttribute("roomName", roomName);
//        model.addAttribute("chatItemsByDate", chatItemsByDate);
        return "chat/each";
    }

    @GetMapping("/getChatItems")
    @ResponseBody
    public ResponseEntity<Map<String, List<ChatItem>>> getChatItems(@RequestParam String userId, @RequestParam long roomId) {
        User user = userService.findByUid(userId);
        ChatRoom chatRoom = chatRoomService.findById(roomId);
        Map<String, List<ChatItem>> chatItemsByDate = chatMessageService.getChatItemsByDate(roomId, user);
        return ResponseEntity.ok(chatItemsByDate);
    }

    @PostMapping("/createChatRoom")
    @ResponseBody
    public String createChatRoom(String memberId, HttpSession session) {
        String uid = (String) session.getAttribute("sessUid");
        User owner = userService.findByUid(uid);
        String names[] = memberId.split(",");
        String roomName = names.length == 1 ? "personal" : "group";
        ChatRoom chatRoom = ChatRoom.builder()
                .name(roomName).owner(owner).timestamp(LocalDateTime.now())
                .build();

        Set<ChatUser> members = new HashSet<>();
        ChatUser chatUser = ChatUser.builder()
                .chatRoom(chatRoom).user(owner).joinedAt(LocalDateTime.now())
                .build();
        members.add(chatUser);
        for (String name: names) {
            User user = userService.findByUid(name.strip());
            chatUser = ChatUser.builder()
                    .chatRoom(chatRoom).user(user).joinedAt(LocalDateTime.now())
                    .build();
            members.add(chatUser);
        }
        chatRoom.setMembers(members);
        chatRoomService.insertChatRoom(chatRoom);
        return "";
    }

    @GetMapping("/users/{roomId}")
    @ResponseBody
    public ResponseEntity<List<UserDto>> getUsers(@PathVariable long roomId, HttpSession session) {
        List<User> users = userService.getUsers();
        ChatRoom chatRoom = chatRoomService.findById(roomId);
        Set<ChatUser> members = chatRoom.getMembers();
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user: users) {
            boolean isMember = user.getChatUsers().stream()
                    .anyMatch(chatUser -> chatUser.getChatRoom().equals(chatRoom));
            UserDto userDto = UserDto.builder()
                    .uid(user.getUid()).uname(user.getUname()).profileUrl(user.getProfileUrl())
                    .isMember(isMember)
                    .build();
            userDtoList.add(userDto);
        }
        return ResponseEntity.ok(userDtoList);
    }

    @PostMapping("/addMembers/{roomId}")
    @ResponseBody
    public ResponseEntity<String> addMembers(@PathVariable long roomId, @RequestBody List<String> selectedUsers) {
        ChatRoom chatRoom = chatRoomService.findById(roomId);
        Set<ChatUser> members = chatRoom.getMembers();
        for (String uid: selectedUsers) {
            User user = userService.findByUid(uid);
            ChatUser chatUser = ChatUser.builder()
                    .chatRoom(chatRoom).user(user).joinedAt(LocalDateTime.now())
                    .build();
            members.add(chatUser);
        }
        if (chatRoom.getName().equals("personal") && members.size() >= 3 )
            chatRoom.setName("group");
        chatRoom.setMembers(members);
        chatRoomService.updateChatRoom(chatRoom);
        return ResponseEntity.ok("");
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
        User user = userService.findByUid(uid);
        List<Chatter> chatterList = chatterService.getChatterList(user);
        model.addAttribute("user", user);
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

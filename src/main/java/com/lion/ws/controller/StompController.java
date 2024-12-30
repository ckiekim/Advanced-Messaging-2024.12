package com.lion.ws.controller;

import com.lion.ws.entity.PersonalMessage;
import com.lion.ws.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StompController {
    @Autowired private MessageService messageService;

    @MessageMapping("/echo")        // 클라이언트가 /app/echo로 메세지를 보냄
    @SendTo("/topic/echoes")        // 메세지는 /topic/messages로 broadcasting
    public String echoMessage(String message) {
//        System.out.println("========== " + message);
        return message;
    }

    @GetMapping("/stomp/echo")
    public String stompMsg() {
        return "stomp/echo";
    }

    @GetMapping("/stomp/echo2")
    public String stompMsg2() {
        return "stomp/echo2";
    }

    @MessageMapping("/personal")        // 클라이언트가 /app/personal로 메세지를 보냄
    public void processMessage(PersonalMessage message) {
        System.out.println(message);
        try {
            messageService.sendMessages(message);
//            System.out.println("Message sent to recipient: " + message.getRecipient());
        } catch (Exception e) {
            System.err.println("Error while sending message: " + e.getMessage());
        }
    }

    @GetMapping("/stomp/personal")
    public String stompPersonal() {
        return "stomp/personal";
    }
}

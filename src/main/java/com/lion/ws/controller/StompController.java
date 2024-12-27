package com.lion.ws.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StompController {

    @MessageMapping("/send")        // 클라이언트가 /app/send로 메세지를 보냄
    @SendTo("/topic/messages")      // 메세지는 /topic/messages로 broadcasting
    public String sendMessage(String message) {
        return message;
    }

    @GetMapping("/stomp")
    public String stompMsg() {
        return "stomp/send";
    }
}

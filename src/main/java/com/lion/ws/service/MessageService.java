package com.lion.ws.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Autowired private RabbitTemplate rabbitTemplate;

    public void sendMessages(String uid, String message) {
        String destination = "/queue/" + uid;
        rabbitTemplate.convertAndSend(destination, message);
    }
}

package com.lion.ws.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lion.ws.entity.PersonalMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Autowired private RabbitTemplate rabbitTemplate;

    public void sendMessages(PersonalMessage message) {
        String destination = message.getRecipient();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonMessage = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(destination, jsonMessage);
        } catch (Exception e) {
            System.err.println("Error serializing message: " + e.getMessage());
        }
    }
}

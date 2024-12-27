package com.lion.ws.controller;

import com.lion.ws.service.RabbitMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rabbit")
public class RabbitController {
    @Autowired private RabbitMQProducer producer;

    @GetMapping("/send")
    @ResponseBody
    public String send() {
        String message = "Hello World!";
        producer.sendMessage(message);
        return "Done";
    }
}

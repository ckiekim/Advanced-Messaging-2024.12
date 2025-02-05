package com.lion.ws.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/notice")
public class NoticeController {
    @GetMapping("/all")
    public String all() {
        return "notice/broadcast";
    }

    @GetMapping(value = "/broadcast", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter broadcast() {
        SseEmitter emitter = new SseEmitter();      // 기본 timeout = 30초, 변경 가능

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd(E) HH:mm:ss", Locale.KOREAN);
            String formattedDateTime = now.format(formatter);
            try {
                emitter.send(formattedDateTime);
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }, 0L, 1L, TimeUnit.SECONDS);

        return emitter;
    }

    @GetMapping(value = "/clock", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter clock() {
        SseEmitter emitter = new SseEmitter();      // 기본 timeout = 30초, 변경 가능

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd(E) HH:mm:ss", Locale.KOREAN);
            String formattedDateTime = now.format(formatter);
            try {
                emitter.send(formattedDateTime);
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }, 0L, 1L, TimeUnit.SECONDS);

        return emitter;
    }

    @GetMapping("/uni")
    public String unicast() {
        return "notice/unicast";
    }

    @GetMapping(value = "/personal/{uid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter personal(@PathVariable String uid) {
        SseEmitter emitter = new SseEmitter();      // 기본 timeout = 30초, 변경 가능

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd(E) HH:mm:ss", Locale.KOREAN);
            String formattedDateTime = now.format(formatter);
            try {
                emitter.send(formattedDateTime + " for " + uid);
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }, 500L, 1000L, TimeUnit.MILLISECONDS);

        return emitter;
    }

    @GetMapping("/double")
    public String doublecast() {
        return "notice/doublecast";
    }

    @GetMapping("/dice")
    public String dice() {
        return "notice/dice";
    }

    @GetMapping(value = "/dice-game", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Object>> diceGame() {
        return Flux.interval(Duration.ofSeconds(3L))
                .map(sequence -> {
                    Map<String, Object> jsonData = new HashMap<>();
                    jsonData.put("id", UUID.randomUUID().toString());
                    jsonData.put("message", "This is dice event " + sequence);
                    jsonData.put("dice", (int)(Math.random()*6 + 1));
                    jsonData.put("timestamp", System.currentTimeMillis());
                    return jsonData;
                });
    }
}

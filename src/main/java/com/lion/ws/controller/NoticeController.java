package com.lion.ws.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
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
}

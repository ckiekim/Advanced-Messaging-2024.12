package com.lion.ws.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chatter {
    private long roomId;
    private String roomName;
    private String roomProfile;
    private String message;
    private String timeStr;
    private int newCount;
}

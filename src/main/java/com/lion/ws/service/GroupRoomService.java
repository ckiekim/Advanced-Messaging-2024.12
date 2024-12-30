package com.lion.ws.service;

import com.lion.ws.entity.GroupRoom;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class GroupRoomService {
    private final Map<String, GroupRoom> groupRooms = new HashMap<>();

    // 단톡방 생성
    public GroupRoom createRoom(String name) {
        String roomId = UUID.randomUUID().toString();
        GroupRoom room = new GroupRoom(roomId, name);
        groupRooms.put(roomId, room);
        return room;
    }

    // 특정 채팅방 조회
    public GroupRoom findRoomByName(String roomName) {
        for (String key: groupRooms.keySet()) {
            if (groupRooms.get(key).getRoomName().equals(roomName))
                return groupRooms.get(key);
        }
        return null;
    }
}

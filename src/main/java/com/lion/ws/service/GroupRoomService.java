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

    // 단톡방 목록 조회
    public  Map<String, GroupRoom> findAllRooms() {
        return groupRooms;
    }

    // 특정 채팅방 조회
    public GroupRoom findRoomById(String roomId) {
        return groupRooms.get(roomId);
    }
}

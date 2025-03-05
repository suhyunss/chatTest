package com.example.chat1.service;

import com.example.chat1.VO.Room;
import com.example.chat1.VO.User;
import com.example.chat1.repository.RoomRepository;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public Object getAllRooms() throws IOException {
        return roomRepository.findAll();
    }

    public Object getUserRooms(String userId) throws IOException {
        return roomRepository.findAll().stream()
                .filter(room -> room.getParticipantList().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .collect(Collectors.toList());
    }

    public Room getRoomByEstimateNum(String estimateNum) throws IOException {
        return  roomRepository.findAll().stream()
                .filter(room -> room.getEstimateNum().equals(estimateNum))
                .findFirst()
                .orElse(null);
    }

    // 참가자 목록에 새로운 사용자 추가
    public boolean addParticipantsToRoom(String estimateNum, List<User> userIds) throws IOException {
        ArrayNode participantsList = roomRepository.getParticipantsByEstimateNum(estimateNum);

        // 새로운 참가자 추가
        for (User user : userIds) {
            if (!containsUserId(participantsList, user.getId())) {  // 중복 방지
                // 새로운 참가자를 JSON 형태로 추가
                ObjectNode userNode = participantsList.addObject();
                userNode.put("id", user.getId());
                userNode.put("userName", user.getUserName());
                userNode.put("authority", user.getAuthority());
            }
        }

        // 업데이트 후 저장
        return roomRepository.saveParticipants(estimateNum, participantsList);
    }

    // 중복된 userId가 있는지 확인하는 메서드
    private boolean containsUserId(ArrayNode participantsList, String userId) {
        for (int i = 0; i < participantsList.size(); i++) {
            if (participantsList.get(i).get("id").asText().equals(userId)) {
                return true;  // 중복된 ID가 존재하면 true
            }
        }
        return false;  // 중복된 ID가 없으면 false
    }
}

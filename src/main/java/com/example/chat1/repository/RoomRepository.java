package com.example.chat1.repository;

import com.example.chat1.VO.Room;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Repository
@Log4j2
public class RoomRepository {

    private static final String FILE_PATH = "src/main/resources/room_data.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 모든 방 목록을 읽는 메소드
    public List<Room> findAll() throws IOException {
        return objectMapper.readValue(new File(FILE_PATH),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Room.class));
    }


    // JSON 파일 읽기
    public JsonNode getJsonData() throws IOException {
        File file = new File(FILE_PATH);
        log.info("getJsonData = {} ", objectMapper.readTree(file));
        return objectMapper.readTree(file);
    }


    // 특정 채팅방의 참가자 목록 가져오기
    public ArrayNode getParticipantsByEstimateNum(String estimateNum) throws IOException {
        JsonNode root = getJsonData();
        JsonNode roomNode = root.findValue("estimateNum");

        if (roomNode != null && roomNode.isArray()) {
            for (JsonNode room : roomNode) {
                if (room.has("estimateNum") && room.get("estimateNum").asText().equals(estimateNum)) {
                    return (ArrayNode) room.get("participantList");
                }
            }
        }
        return objectMapper.createArrayNode();  // 참가자 목록이 없으면 빈 배열 반환
    }

    // 참가자 목록 업데이트 후 저장
    public boolean saveParticipants(String estimateNum, ArrayNode updatedParticipants) throws IOException {
        File file = new File(FILE_PATH);
        JsonNode root = getJsonData();

        if (root.isArray()) {
            for (JsonNode roomNode : root) {
                if (roomNode.has("estimateNum") && roomNode.get("estimateNum").asText().equals(estimateNum)) {
                    ((ObjectNode) roomNode).set("participantList", updatedParticipants);
                    objectMapper.writeValue(file, root);  // JSON 파일에 변경된 내용 저장
                    return true;
                }
            }
        }
        // 파일에 데이터가 없거나, 방을 찾지 못한 경우 예외 처리
        return false;
    }
}

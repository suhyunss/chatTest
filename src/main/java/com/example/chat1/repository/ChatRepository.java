package com.example.chat1.repository;

import com.example.chat1.VO.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Repository
@Log4j2
public class ChatRepository {

    private static final String FILE_PATH = "src/main/resources/chat_data.json";
    private final ObjectMapper objectMapper = new ObjectMapper();


    // 전체 채팅 내역 불러오기
    public List<ChatMessage> findAll() throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return Collections.emptyList(); // 파일이 없으면 빈 리스트 반환
        }
        return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, ChatMessage.class));
    }

    // 메시지 저장
    public boolean saveChatMessage(ChatMessage chatMessage) throws IOException {

        File file = new File(FILE_PATH);

        try {
            List<ChatMessage> chatMessages = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, ChatMessage.class));

            chatMessages.add(chatMessage);

            objectMapper.writeValue(file, chatMessages);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

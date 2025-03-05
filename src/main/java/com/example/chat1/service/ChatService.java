package com.example.chat1.service;

import com.example.chat1.VO.ChatMessage;
import com.example.chat1.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    public List<ChatMessage> findByEstimateNum(String estimateNum) throws IOException {
        if (estimateNum == null) {
            return Collections.emptyList(); // estimateNum이 null이면 빈 리스트 반환
        }
        return chatRepository.findAll().stream()
                .filter(chatMessage -> estimateNum.equals(chatMessage.getEstimateNum())) // null 체크 안전하게 처리
                .collect(Collectors.toUnmodifiableList());
    }

    public boolean saveChatMessage(ChatMessage chatMessage) throws IOException {
        return chatRepository.saveChatMessage(chatMessage);
    }

}

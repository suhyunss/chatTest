package com.example.chat1.handler;

import com.example.chat1.VO.ChatMessage;
import com.example.chat1.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;

@Log4j2
@EnableWebSocketMessageBroker
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    public ChatService chatService;

    private ChatRoomSessionManager chatRoomSessionManager = new ChatRoomSessionManager();  // 방 세션 관리 객체

    public ChatWebSocketHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String estimateNum = getEstimateNumFromSession(session);

        if (estimateNum == null) {
            log.info("WebSocket 연결 실패: estimateNum을 찾을 수 없음");
            session.close();
            return;
        }

        // 방에 세션을 추가
        log.info("WebSocket 연결 성공 - 견적번호: " + estimateNum);
        chatRoomSessionManager.addSessionToRoom(estimateNum, session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

        // 메시지 내용은 그대로 전달하고, 해당 방에 속한 모든 사용자에게 메시지 전송
        String estimateNum = getEstimateNumFromSession(session);  // 세션에서 방 번호 추출
        if (estimateNum != null) {
            // 해당 방에 속한 모든 사용자에게 메시지 전송
            chatRoomSessionManager.sendMessageToRoom(estimateNum, message);

        }

        String messagePayload = message.getPayload();

        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessage chatMessage = objectMapper.readValue(messagePayload, ChatMessage.class);
        chatMessage.setEstimateNum(estimateNum);
        log.info("받은 메시지 : {} ", chatMessage);

        chatService.saveChatMessage(chatMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String estimateNum = getEstimateNumFromSession(session);  // 세션에서 방 번호 추출
        if (estimateNum != null) {
            // 방에서 세션 제거
            chatRoomSessionManager.removeSessionFromRoom(estimateNum, session);
        }
    }

    // 세션에서 방 번호를 추출
    private String getEstimateNumFromSession(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri != null) {
            String path = uri.getPath();
            String[] parts = path.split("/");
            if (parts.length > 2) {
                return parts[2];  // 방 번호 추출
            }
        }
        return null;
    }
}

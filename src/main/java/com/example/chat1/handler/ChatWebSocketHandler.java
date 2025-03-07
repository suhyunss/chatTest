package com.example.chat1.handler;

import com.example.chat1.VO.ChatMessage;
import com.example.chat1.VO.User;
import com.example.chat1.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Log4j2
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final ChatRoomSessionManager chatRoomSessionManager = new ChatRoomSessionManager();

    public ChatWebSocketHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String estimateNum = getEstimateNumFromSession(session);

        if (estimateNum == null) {
            log.error("[WebSocket 연결 실패] estimateNum을 찾을 수 없음. 세션 닫음");
            session.close();
            return;
        }

        // HttpSession을 가져오기 위해 HttpServletRequest 사용
        HttpServletRequest request = (HttpServletRequest) session.getAttributes().get("HTTP_SESSION");

        if (request != null) {
            HttpSession httpSession = request.getSession(false); // 기존 세션 찾기
            if (httpSession != null) {
                // 세션에 저장된 사용자 정보 가져오기
                User user = (User) httpSession.getAttribute("user");
                if (user != null) {
                    // WebSocketSession에 사용자 정보 저장
                    session.getAttributes().put("user", user);
                    log.info("WebSocket 연결 성공 - 사용자: " + user.getId());
                }
                if (user == null) {
                    log.info("WebSocket 연결 실패: 사용자 정보를 찾을 수 없음");
                    session.close();
                    return;
                }
            }
        }


        log.info("[WebSocket 연결 성공] 견적번호: {}, 세션 ID: {}", estimateNum, session.getId());
        chatRoomSessionManager.addSessionToRoom(estimateNum, session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

        // 메시지 내용은 그대로 전달하고, 해당 방에 속한 모든 사용자에게 메시지 전송
        String estimateNum = getEstimateNumFromSession(session); // 세션에서 방 번호 추출
        if (estimateNum == null) {
            log.error("[WebSocket 오류] estimateNum을 찾을 수 없음. 메시지 전송 취소.");
            return;
        }

        log.info("[메시지 수신] 견적번호: {}, 세션 ID: {}, 메시지: {}", estimateNum, session.getId(), message.getPayload());

        try {
            // 해당 방의 모든 사용자에게 메시지 전송
            chatRoomSessionManager.sendMessageToRoom(estimateNum, message);
        } catch (Exception e) {
            log.error("[WebSocket 오류] 메시지 전송 중 예외 발생: {}", e.getMessage(), e);
        }

        // 메시지 저장
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
            chatMessage.setEstimateNum(estimateNum);
            chatService.saveChatMessage(chatMessage);
        } catch (Exception e) {
            log.error("[메시지 저장 오류] JSON 파싱 또는 저장 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        String estimateNum = getEstimateNumFromSession(session);
        if (estimateNum != null) {
            // 방에서 세션 제거
            chatRoomSessionManager.removeSessionFromRoom(estimateNum, session);
            log.info("[WebSocket 연결 종료] 견적번호: {}, 세션 ID: {}, 상태: {}", estimateNum, session.getId(), closeStatus);
        } else {
            log.error("[WebSocket 종료 오류] estimateNum을 찾을 수 없음.");
        }
    }

    // 세션에서 방 번호를 추출
    private String getEstimateNumFromSession(WebSocketSession session) {
        try {
            String path = session.getUri().getPath();
            String[] parts = path.split("/");
            return (parts.length > 2) ? parts[2] : null;
        } catch (Exception e) {
            log.error("URI에서 estimateNum 추출 실패", e);
            return null;
        }
    }
}

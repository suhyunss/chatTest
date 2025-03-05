package com.example.chat1.handler;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRoomSessionManager {

    // 방 번호를 키로, 해당 방에 있는 WebSocketSession 리스트를 값으로 저장
    private Map<String, List<WebSocketSession>> roomSessions = new HashMap<>();

    // 방에 세션을 추가하는 메서드
    public void addSessionToRoom(String roomId, WebSocketSession session) {
        roomSessions.computeIfAbsent(roomId, k -> new ArrayList<>()).add(session);
    }

    // 방에서 세션을 제거하는 메서드
    public void removeSessionFromRoom(String roomId, WebSocketSession session) {
        List<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
        }
    }

    // 방에 있는 모든 사용자에게 메시지 전송
    public void sendMessageToRoom(String roomId, TextMessage message) throws IOException {
        List<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                session.sendMessage(message);
            }
        }
    }
}

package com.example.chat1.handler;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomSessionManager {

    // 방 번호를 키로, 해당 방에 있는 WebSocketSession 리스트를 값으로 저장 (중복제거)
    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    // 방에 세션을 추가하는 메서드
    public void addSessionToRoom(String roomId, WebSocketSession session) {
        roomSessions.computeIfAbsent(roomId, k -> Collections.synchronizedSet(new HashSet<>())).add(session);
    }

    // 방에서 세션을 제거하는 메서드
    public void removeSessionFromRoom(String roomId, WebSocketSession session) {
        Set<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                roomSessions.remove(roomId);  // 방에 남은 세션이 없으면 방 제거
            }
        }
    }

    // 방에 있는 모든 사용자에게 메시지 전송 (세션이 닫히지 않았는지 확인)
    public void sendMessageToRoom(String roomId, TextMessage message) throws IOException {
        Set<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            for (WebSocketSession session : new HashSet<>(sessions)) {
                if (session.isOpen()) {  // 세션이 열려 있는 경우만 메시지 전송
                    session.sendMessage(message);
                }
            }
        }
    }
}

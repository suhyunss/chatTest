package com.example.chat1.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    private String id;          // 메세지 KEY
    private String estimateNum; // 견적번호
    private User user;          // 보낸이 정보
    private String content;     // 내용
    private String time;        // 보낸시간
}

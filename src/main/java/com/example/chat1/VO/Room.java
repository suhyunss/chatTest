package com.example.chat1.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    private String roomId;
    private String estimateNum;         // 견적번호
    private List<User> participantList; // 참여자리스트
}

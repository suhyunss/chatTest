package com.example.chat1.VO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String id;              // 아이디
    private String userName;        // 이름
    private String authority;       // 권한

    @JsonCreator
    public User(@JsonProperty("id") String id) {
        this.id = id;
    }
}

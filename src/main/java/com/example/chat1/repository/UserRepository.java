package com.example.chat1.repository;


import com.example.chat1.VO.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Repository
public class UserRepository {

    private static final String FILE_PATH = "./data/user_data.json";
//    private final String filePath = "src/main/resources/user_data.json";

    public List<User> getAllUsers() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(FILE_PATH),
                objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
    }
}

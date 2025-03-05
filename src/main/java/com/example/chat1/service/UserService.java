package com.example.chat1.service;


import com.example.chat1.VO.User;
import com.example.chat1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAdmins() throws IOException {
        return userRepository.getAllUsers().stream()
                .filter(user -> "ADMIN".equals(user.getAuthority()))
                .collect(Collectors.toList());
    }

    public List<User> getCaps() throws IOException {
        return userRepository.getAllUsers().stream()
                .filter(user -> "CAP".equals(user.getAuthority()))
                .collect(Collectors.toList());
    }

    public List<User> getAgs() throws IOException {
        return userRepository.getAllUsers().stream()
                .filter(user -> "AG".equals(user.getAuthority()))
                .collect(Collectors.toList());
    }

    public User getUserById(String userId) throws IOException {
        List<User> users = userRepository.getAllUsers();  // 모든 사용자 데이터 읽어오기
        return users.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElse(null);  // 해당 ID가 없으면 null 반환
    }

    // 주어진 userId 목록을 사용하여 사용자들을 반환하는 메서드
    public List<User> getUsersByIds(List<String> userIds) throws IOException {
        List<User> allUsers = userRepository.getAllUsers();  // 사용자 목록을 가져옵니다 (예: DB나 JSON 파일에서)

        return allUsers.stream()
                .filter(user -> userIds.contains(user.getId()))  // userIds에 포함된 id만 필터링
                .collect(Collectors.toList());
    }
}

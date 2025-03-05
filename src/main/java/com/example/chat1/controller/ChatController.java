package com.example.chat1.controller;

import com.example.chat1.VO.ChatMessage;
import com.example.chat1.VO.Room;
import com.example.chat1.VO.User;
import com.example.chat1.service.ChatService;
import com.example.chat1.service.RoomService;
import com.example.chat1.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Log4j2
public class ChatController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final RoomService roomService;

    @Autowired
    private final ChatService chatService;

    public ChatController(UserService userService, RoomService roomService, ChatService chatService) {
        this.userService = userService;
        this.roomService = roomService;
        this.chatService = chatService;
    }

    @GetMapping("/")
    public String home(HttpSession session, Model model) throws IOException {
        log.info("⭐ 기본 화면");

        List<Map.Entry<String, List<User>>> userGroups = List.of(
                new AbstractMap.SimpleEntry<>("AG", userService.getAgs()),
                new AbstractMap.SimpleEntry<>("CAP", userService.getCaps()),
                new AbstractMap.SimpleEntry<>("ADMIN", userService.getAdmins())
        );



        model.addAttribute("userGroups", userGroups);
        return "home";
    }

    @ResponseBody
    @PostMapping("/login.do")
    public String login(HttpSession session, @RequestParam("userId") String userId) throws IOException {
        log.info("로그인 시도 : {}", userId);

        session.removeAttribute("user");
        User user = userService.getUserById(userId);

        if (user != null) {
            session.setAttribute("user", user);
            return "SUCCESS";
        } else {
            return "FAIL";
        }
    }

    @GetMapping("/list.do")
    public String list(HttpSession session, Model model) throws IOException {

        User user = (User) session.getAttribute("user");
        log.info("리스트 화면 로그인 Id = {}", user != null ? user.getId() : "No User");

        if (user == null) {
            return "redirect:/";
        }

        model.addAllAttributes(Map.of(
                "user", user,
                "allRooms", roomService.getAllRooms(),
                "userRooms", roomService.getUserRooms(user.getId())
        ));

        return "list";
    }

    @GetMapping("/createRoom.do")
    public String createRoom(@RequestParam("estimateNum") String estimateNum, Model model) throws IOException {
        log.info("🚀 방 만들기 - 견적번호: {}", estimateNum);
        List<User> participants = List.of();

        Room room = roomService.getRoomByEstimateNum(estimateNum);
        if(room != null) {
            participants = room.getParticipantList();
        }

        model.addAllAttributes(Map.of(
                "room", room,
                "agList", userService.getAgs(),
                "capList", userService.getCaps(),
                "agUsers", classifyUsers(userService.getAgs(), participants),   // 참여/비참여 AG사용자
                "capUsers", classifyUsers(userService.getCaps(), participants)      // 참여/비참여 CAP사용자
        ));

        return "createRoom";
    }

    @ResponseBody
    @PostMapping("/addParticipantsToRoom.do")
    public Map<String, Object> addParticipantsToRoom(@RequestBody Map<String, Object> requestBody) throws IOException {

        String estimateNum = (String) requestBody.get("estimateNum");
        List<String> userIds = (List<String>) requestBody.get("userIds");

        log.info("🚀🚀 (초대) 견적번호: {}, id : {}", estimateNum, userIds.toString());

        List<User> users = userService.getUsersByIds(userIds);

        roomService.addParticipantsToRoom(estimateNum, users);

        Map<String, Object> response = new HashMap<>();
        response.put("estimateNum", estimateNum);
        return response;
    }

    /*
     * 사용자 리스트를 방 참여 여부에 따라 포함/미포함으로 분류
     */
    public Map<String, List<User>> classifyUsers(List<User> users, List<User> participants) {
        // 방에 포함된 사용자
        List<User> included = users.stream().filter(user -> participants.stream().anyMatch(participant -> participant.getId().equals(user.getId()))).collect(Collectors.toList());

        // 방에 포함되지 않은 사용자
        List<User> excluded = users.stream().filter(user -> participants.stream().noneMatch(participant -> participant.getId().equals(user.getId()))).collect(Collectors.toList());

        return Map.of("included", included, "excluded", excluded);
    }


    /*
     * 채팅방
     */
    @GetMapping("/{estimateNum}/chat.do")
    public String chat(@PathVariable String estimateNum, Model model, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", new ObjectMapper().writeValueAsString(user));
        return "chat";
    }

    /**
     * 채팅 내역 가져오기
     */
    @GetMapping("/{estimateNum}/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable("estimateNum") String estimateNum) throws IOException {
        // 채팅 내역을 가져오기
        List<ChatMessage> chatMessages = chatService.findByEstimateNum(estimateNum);
        return ResponseEntity.ok(chatMessages); // JSON으로 반환
    }


}
package com.ccp5.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccp5.dto.User;
import com.ccp5.repository.UserRepository;
import com.ccp5.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class); // 로거 선언
    @Autowired
    private  UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public UserController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @GetMapping("/member/login") // 로그인 페이지 경로 수정
    public String login() {
        logger.info("Accessing login page"); // 로그 출력
        return "member/login";
    }
    
    @PostMapping("/loginPro")
    public ResponseEntity<String> loginProcess(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) {
        logger.info("Attempting login for username: {}", username); // 로그 출력
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // 세션에 사용자 이름을 저장합니다.
            session.setAttribute("username", username);

            // 로그인 성공 후 리다이렉트할 페이지를 지정합니다.
            return new ResponseEntity<>("Login successful", HttpStatus.OK); // 로그인 성공
        } else {
            // 로그인 실패 시 실패 이유를 반환합니다.
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED); // 로그인 실패
        }
    }

    @GetMapping("/api/join")
    public String joinForm() {
        logger.info("Accessing join form page"); // 로그 출력
        return "member/join";
    }

    @PostMapping("/api/join")
    public ResponseEntity<String> join(@RequestBody User user) {
        logger.info("Attempting to join with username: {}", user.getUsername()); // 로그 출력
        if (userRepository.findByUsername(user.getUsername()) != null) {
            logger.warn("Username {} already exists", user.getUsername()); // 로그 출력
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists"); // 이미 존재하는 사용자
        }
        // 비밀번호를 암호화하여 저장

        userService.join(user);
        logger.info("User {} successfully joined", user.getUsername()); // 로그 출력

        // 응답 데이터를 Map 형식으로 구성
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "User successfully joined");
        responseMap.put("username", user.getUsername());

        // ObjectMapper를 사용하여 Map을 JSON 문자열로 변환
        String jsonResponse;
        try {
            jsonResponse = objectMapper.writeValueAsString(responseMap);
        } catch (JsonProcessingException e) {
            log.error("Error converting response to JSON", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error converting response to JSON");
        }

        // ResponseEntity를 사용하여 JSON 형식의 응답 반환
        return ResponseEntity.ok(jsonResponse);
    }
}


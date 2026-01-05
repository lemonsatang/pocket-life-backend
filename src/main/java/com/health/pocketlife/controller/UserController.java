package com.health.pocketlife.controller;

import com.health.pocketlife.dto.UserDTO;
import com.health.pocketlife.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;



    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody UserDTO userDTO){

        try{
            userService.join(userDTO);
            // 1. 성공했을 때: 200 OK 상태코드 + "회원가입 성공"이라는 문자열을 보냄
            return ResponseEntity.ok("회원가입 성공!");
        } catch (Exception e) {
            // 2. 실패했을 때: 400 Bad Request 상태코드 + 에러 메시지를 보냄
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 실패");
        }
    }

    // 아이디 중복 확인
    @PostMapping("/idChk")
    public ResponseEntity<Boolean> idChk(@RequestBody UserDTO userDTO){
        boolean isDuplicate = userService.idChk(userDTO.getUsrid());

        return ResponseEntity.ok(isDuplicate);
    }

}

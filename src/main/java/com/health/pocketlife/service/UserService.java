package com.health.pocketlife.service;

import com.health.pocketlife.dto.UserDTO;
import com.health.pocketlife.entity.Role;
import com.health.pocketlife.entity.User;
import com.health.pocketlife.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void join(UserDTO userDTO){
        // 아이디 중복 검증
        if(userRepository.existsByUsrid(userDTO.getUsrid())){
            return;
        }

        User data = User.builder()
                .usrid(userDTO.getUsrid())
                .usrnm(userDTO.getUsrnm())
                .passwd(passwordEncoder.encode(userDTO.getPasswd()))
                .email(userDTO.getEmail())
                .tel(userDTO.getTel())
                .birth(userDTO.getBirth())
                .mkConsent(userDTO.getMkConsent()) // 추가
                .mailConsent(userDTO.getMailConsent()) // 추가
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(data);
    }


    public boolean idChk(String usrid) {
        return userRepository.existsByUsrid(usrid);
    }

    public String findId(String usrnm, String tel) {
        String pureNumbers = tel.replaceAll("[^0-9]", "");

        // 2. 숫자를 DB 형식(010-1111-2222)으로 변환
        String formattedTel = pureNumbers.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-$3");

        return userRepository.findByUsrnmAndTel(usrnm, formattedTel)
                .map(user -> {
                    String userId = user.getUsrid();
                    return userId.substring(0, 3) + "*".repeat(userId.length() - 3);
                })
                .orElse("fail");
    }
}

package com.health.pocketlife.service;

import com.health.pocketlife.entity.User;
import com.health.pocketlife.jwt.CustomUserDetails;
import com.health.pocketlife.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 유저가 없으면 바로 예외처리
        User userData = userRepository.findByUsrid(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 유저가 있으면 정보 반환
        return new CustomUserDetails(userData);
    }
}

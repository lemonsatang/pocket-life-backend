package com.health.pocketlife.jwt;

import com.health.pocketlife.entity.Role;
import com.health.pocketlife.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. 헤더에서 Authorization 추출
        String authorization = request.getHeader("Authorization");

        // 2. 토큰이 없거나 형식이 맞지 않으면 다음 필터로 넘김
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " 제거 후 순수 토큰만 추출
        String token = authorization.split(" ")[1];

        // 4. 토큰 만료 여부 확인
        if (jwtUtil.isExpired(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 5. 토큰에서 유저 정보 획득
        String username = jwtUtil.getUsrid(token);
        String role = jwtUtil.getRole(token);

        // 6. 일회성 인증 세션 생성
        User user = User.builder()
                .usrid(username)
                .role(Role.valueOf(role))
                .passwd("temp_pw") // 비밀번호는 검증에 사용되지 않음
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // 시큐리티 컨텍스트에 인증 정보 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}

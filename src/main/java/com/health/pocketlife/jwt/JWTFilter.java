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

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];

        // [디버그 로그] 토큰 검증 시작
        System.out.println("DEBUG: Token received: " + token.substring(0, 10) + "...");

        if (jwtUtil.isExpired(token)) {
            System.out.println("DEBUG: Token is expired or invalid.");
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtUtil.getUsrid(token);
        String role = jwtUtil.getRole(token);
        
        System.out.println("DEBUG: User verified. Username: " + username + ", Role: " + role);

        User user = User.builder()
                .usrid(username)
                .role(Role.valueOf(role)) // Enum에는 USER만 저장되어 있을 것이므로
                .passwd("temp_pw")
                .build();

        // CustomUserDetails가 내부적으로 ROLE_ 접두사를 붙여 권한을 생성하도록 설계됨
        CustomUserDetails customUserDetails = new CustomUserDetails(user);


        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customUserDetails,
                null,
                customUserDetails.getAuthorities() // 여기서 ROLE_USER가 반환됨
        );

        // [2026-01-16 최종 점검] 인증 객체 생성 직후 권한 로그 출력
        System.out.println("DEBUG: Auth Success. Authorities: " + customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);

    }
}

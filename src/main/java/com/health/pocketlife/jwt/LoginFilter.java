package com.health.pocketlife.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        // 중요: 프론트엔드(리액트)에서 보내는 JSON/FormData의 키값을 일치시킴
        setUsernameParameter("username");
        setPasswordParameter("password");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 1. 프론트에서 보낸 ID, PW 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        System.out.println("로그인 시도 - ID: " + username + ", PW: " + (password != null ? "입력됨" : "null"));

        // 2. 시큐리티용 토큰 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        // 3. 인증 매니저에게 검증 위임
        return authenticationManager.authenticate(authToken);
    }

    // 로그인 성공 시 JWT 발급
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        String username = authResult.getName();
        String role = authResult.getAuthorities().iterator().next().getAuthority();

        // JWT 생성 (유효기간 10시간)
        String token = jwtUtil.createJwt(username, role, 36000000L);

        // 헤더에 Bearer 토큰 추가
        response.addHeader("Authorization", "Bearer " + token);
        response.setStatus(HttpServletResponse.SC_OK);

        System.out.println("로그인 성공! 유저: " + username);
    }

    // 로그인 실패 시 처리
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        System.out.println("로그인 실패 이유: " + failed.getMessage());
        response.setStatus(401);
    }
}
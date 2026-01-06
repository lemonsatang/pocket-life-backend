package com.health.pocketlife.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
        // 리액트 FormData의 키값("username", "password")을 시큐리티가 인식하도록 설정
        setUsernameParameter("username");
        setPasswordParameter("password");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)throws AuthenticationException {
        // 1. 프론트에서 보낸 ID, PW 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        // 로그를 찍어서 데이터가 들어오는지 확인해봐! (안 들어오면 null이 찍힐 거야)
        System.out.println("아이디: " + username);

        // 2. 시큐리티에서 사용하기 위해 Token에 담음
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        // 3. 검증 시작 (AuthenticationManager에게 넘김)
        return authenticationManager.authenticate(authToken);
    }

    // 로그인 성공시
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        String username = authResult.getName();

        String role = authResult.getAuthorities().iterator().next().getAuthority();

        // 토큰 생성, 유효시간 10시간
        String token = jwtUtil.createJwt(username, role, 36000000L);

        // http 헤더에 담아서 프론트엔드로 전달
        response.addHeader("Authorization", "Bearer " + token);
        response.setStatus(HttpServletResponse.SC_OK);

        // 4. 로그인 성공 시 여기서 JWT 토큰을 발급해서 응답헤더에 넣어줄 거야 (다음 단계)
        System.out.println("로그인 성공!"+ username);
    }

    // 로그인 실패시
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
        System.out.println("로그인 실패: " + failed.getMessage());
    }
}

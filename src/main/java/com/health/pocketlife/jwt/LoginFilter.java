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

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)throws AuthenticationException {
        // 1. 프론트에서 보낸 ID, PW 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        System.out.println("필터가 받은 ID: " + username);
        System.out.println("필터가 받은 PW: " + password);

        // 2. 시큐리티에서 사용하기 위해 Token에 담음
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        // 3. 검증 시작 (AuthenticationManager에게 넘김)
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        String username = authResult.getName();

        String role = authResult.getAuthorities().iterator().next().getAuthority();

        // 토큰 생성, 유효시간 10시간
        String token = jwtUtil.createJwt(username, role, 36000000L);

        // http 헤더에 담아서 프론트엔드로 전달
        response.addHeader("Authorization", "Bearer " + token);

        // 4. 로그인 성공 시 여기서 JWT 토큰을 발급해서 응답헤더에 넣어줄 거야 (다음 단계)
        System.out.println("로그인 성공!");
    }
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        // 여기서 에러 메시지를 확인하면 왜 403이 나는지 알 수 있습니다.
        System.out.println("로그인 실패 이유: " + failed.getMessage());
        response.setStatus(401); // 실패 시 401을 반환하도록 명시
    }
}

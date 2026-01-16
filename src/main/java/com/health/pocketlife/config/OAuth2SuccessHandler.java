package com.health.pocketlife.config;

import com.health.pocketlife.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String usrid = oAuth2User.getAttributes().get("id").toString();

        // 1. JWT 토큰 생성 (유효기간 1시간)
        String token = jwtUtil.createJwt(usrid, "ROLE_USER", 60*60*1000L);

        // 2. 프론트엔드(React)로 토큰을 들고 리다이렉트
        response.sendRedirect("http://localhost:5173/oauth2/redirect?token=" + token);
    }
}

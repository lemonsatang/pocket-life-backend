package com.health.pocketlife.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    // 1. 시크릿 키 생성
    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 2. 토큰 생성
    public String createJwt(String username, String role, Long expiredMs){
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) // 토큰 발급일
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 토큰 만료기간 설정
                .signWith(secretKey) // 시크릿 키 이용하여 위조 방지
                .compact();
    }

    // 3. 가지고 온 토큰이 유효한지 확인
    public Boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // 여기 빨간줄이면 라이브러리 버전 확인!
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration() // 토큰 만료시간 확인
                .before(new Date());
    }
}

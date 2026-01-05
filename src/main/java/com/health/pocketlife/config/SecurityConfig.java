package com.health.pocketlife.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CORS 설정 연결
        http.cors((cors) -> cors.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173")); // 리액트 주소 허용
                configuration.setAllowedMethods(Collections.singletonList("*")); // 모든 HTTP 메서드 허용
                configuration.setAllowCredentials(true); // 자격 증명 허용
                configuration.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더 허용
                configuration.setMaxAge(3600L); // 캐싱 시간

                // 응답 헤더에 Authorization(JWT)을 노출하도록 설정 (나중에 필요함)
                configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                return configuration;
            }
        }));

        http.csrf((auth) -> auth.disable());
        http.formLogin((auth) -> auth.disable());
        http.httpBasic((auth) -> auth.disable());

        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/join", "/login", "/idChk").permitAll()
                .anyRequest().authenticated());

        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화 객체 생성
    }
}

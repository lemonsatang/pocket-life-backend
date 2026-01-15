package com.health.pocketlife.config;

import com.health.pocketlife.jwt.JWTFilter;
import com.health.pocketlife.jwt.JWTUtil;
import com.health.pocketlife.jwt.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 1. CORS 설정
        http.cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
                config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
                config.setAllowCredentials(true);
                config.setExposedHeaders(Collections.singletonList("Authorization"));
                config.setMaxAge(3600L);
                return config;
            }
        }));

        // 2. 기본 보안 설정 비활성화
        http.csrf(csrf -> csrf.disable());
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

        // 3. 권한 설정
        http.authorizeHttpRequests(auth -> auth
                // 모든 경로의 OPTIONS 요청(CORS 검사용)을 허용함
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/login", "/join", "/idChk").permitAll()
                // [수정] 기존 ROLE_USER 권한 체크 -> .authenticated() 로 변경하여 로그인한 모든 유저 허용
                // [2026-01-16 최종 점검] 403 원인 규명을 위해 일시적으로 해당 경로 보안 해제 (permitAll)
                // 테스트 후 반드시 .authenticated()로 복구 필요
                .requestMatchers("/api/tx/**", "/api/todo/**", "/api/stats/**").permitAll()
                .anyRequest().authenticated()
        );

        // 4. 세션 설정 (Stateless)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 5. 필터 추가
        http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        http.addFilterAt(
                new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
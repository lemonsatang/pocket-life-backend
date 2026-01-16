package com.health.pocketlife.service;

import com.health.pocketlife.entity.Role;
import com.health.pocketlife.entity.User;
import com.health.pocketlife.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 카카오에서 준 정보 뽑기
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");
        String providerId = attributes.get("id").toString();

        // 우리 DB에 저장하거나 업데이트하기
        User user = userRepository.findByUsrid(providerId)
                .map(entity -> { // 이미 있으면 업데이트
                    return User.builder().usrid(providerId).usrnm(nickname).email(email).role(Role.ROLE_USER)
                            .provider("kakao").providerId(providerId).build()
                            ;
                })
                .orElse(User.builder() // 없으면 새로 생성(가입)
                        .usrid(providerId).usrnm(nickname).email(email).role(Role.ROLE_USER).provider("kakao")
                        .providerId(providerId).build());

        userRepository.save(user);
        return oAuth2User;
    }
}

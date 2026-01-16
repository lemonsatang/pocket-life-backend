package com.health.pocketlife.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 생성
@Builder // 클래스 레벨로 이동하여 모든 필드에 대한 빌더 메서드 생성
@Table(name = "user")
public class User {

    @Id
    @Column(name = "usrid", length = 20)
    private String usrid; // 아이디(pk)

    @Column(name = "usrnm", nullable = false, length = 10)
    private String usrnm; // 이름

    @Column(name = "passwd", nullable = true, length = 100)
    private String passwd; // 비밀번호, 소셜 로그인을 위해 비밀번호 null 허용

    @Column(length = 40)
    private String email;

    @Column(length = 20)
    private String tel;

    @Column(length = 10)
    private String birth;

    @Column(name = "mk_consent", length = 1)
    private String mkConsent = "N"; // 마케팅 동의 (기본값 N)

    @Column(name = "mail_consent", length = 1)
    private String mailConsent = "N"; // 메일 동의 (기본값 N)

    @Column(length = 20)
    private String provider; // 일반가입:local,  소셜:kakao, naver, google...
    
    @Column(name = "provider_id")
    private String providerId; // 소셜 서비스에서 넘겨주는 고유 식별번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Builder
    public User(String usrid, String usrnm, String passwd, String email, String tel, String birth, Role role) {
        this.usrid = usrid;
        this.usrnm = usrnm;
        this.passwd = passwd;
        this.email = email;
        this.tel = tel;
        this.birth = birth;
        this.role = role;
    }

}

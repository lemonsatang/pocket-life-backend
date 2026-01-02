package com.health.pocketlife.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private String usrid;
    private String usrnm;
    private String passwd;
    private String email;
    private String tel;
    private String birth;

    // 마케팅 및 메일 수신 동의 (보통 "Y" 또는 "N"으로 받음)
    private String mkConsent;
    private String mailConsent;
}

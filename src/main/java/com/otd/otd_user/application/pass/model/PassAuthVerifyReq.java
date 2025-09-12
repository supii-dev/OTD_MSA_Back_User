package com.otd.otd_user.application.pass.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PassAuthVerifyReq {
    private String authCode;    // 인증 코드
    private String state;       // CSRF 방지 상태값
    private String provider;    // PASS 제공사 (SKT, KT, LGU, PAYCO, SAMSUNG)
    private String redirectUri; // 리다이렉트 URI
}

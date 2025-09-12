package com.otd.otd_user.application.pass.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PassAuthResult {
    private boolean success;
    private String name;
    private String phone;
    private String birthDate;
    private String gender;
    private String ci;
    private String di;
    private String authToken;
    private String provider;
    private String errorMessage; // 실패 시 에러 메시지
}

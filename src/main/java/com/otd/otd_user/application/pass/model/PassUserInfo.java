package com.otd.otd_user.application.pass.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PassUserInfo {
    private String name;
    private String phone;
    private String birthDate;
    private String gender;
    private String ci;
    private String di;
}

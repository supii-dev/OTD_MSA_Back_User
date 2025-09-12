package com.otd.otd_user.application.user.model;

import jakarta.validation.constraints.Pattern;

public class UserUpdateReq {
    @Pattern(regexp = "^[가-힣]{2,10}$", message = "닉네임은 한글로 2~10자까지 가능합니다.")
    private String nickName;
    private String phone;
}
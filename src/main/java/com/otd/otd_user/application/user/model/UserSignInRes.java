package com.otd.otd_user.application.user.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSignInRes {
    private Long userId;
    private String nickName;
    private String pic;
}

package com.otd.otd_user.application.user.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserLoginRes {
    private Long userId;
    private String nickName;
    private String pic;
    private String email;
    private List<String> roles;
    private LocalDateTime lastLoginAt;
}

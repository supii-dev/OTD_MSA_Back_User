package com.otd.otd_user.application.user.model;

import com.otd.configuration.enumcode.model.EnumChallengeRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserLoginRes {
    private Long userId;
    private String name;
    private String nickName;
    private String pic;
    private String email;
    private int point;
    private int xp;
    private List<String> roles;
    private LocalDateTime lastLoginAt;
    private EnumChallengeRole challengeRole;
}

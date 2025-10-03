package com.otd.otd_admin.application.admin.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class AdminUserGetRes {
    private long userId;
    private String name;
    private String email;
    private String gender;
    private int level;
    private String phone;
    private int point;
    private String uid;
    private int xp;
    private LocalDateTime createdAt;
}

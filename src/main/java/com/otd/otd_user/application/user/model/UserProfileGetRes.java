package com.otd.otd_user.application.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileGetRes {
    private Long userId;
    private String pic;
    private LocalDateTime createdAt;  // String에서 LocalDateTime으로 변경 권장
    private String uid;
    private String name;
    private String nickName;
    private String email;
    private String phone;
    private String birthDate;
    private String gender;
    private LocalDateTime lastLoginAt;
    private int point;

    // String 타입의 createdAt을 사용하려면 이 생성자를 사용
    public UserProfileGetRes(Long userId, String pic, String createdAt, String uid, String nickName) {
        this.userId = userId;
        this.pic = pic;
        this.createdAt = LocalDateTime.parse(createdAt); // 또는 적절한 파싱 로직
        this.uid = uid;
        this.nickName = nickName;
    }
}
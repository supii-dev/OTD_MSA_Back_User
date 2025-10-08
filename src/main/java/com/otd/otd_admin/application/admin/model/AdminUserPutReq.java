package com.otd.otd_admin.application.admin.model;

import com.otd.configuration.enumcode.model.EnumChallengeRole;
import com.otd.configuration.enumcode.model.EnumUserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserPutReq {
    private Long userId;
    private String name;
    private String nickName;
    private int point;
    private int xp;
    private String password;
    private EnumChallengeRole challengeRole;
    private EnumUserRole userRole;

}

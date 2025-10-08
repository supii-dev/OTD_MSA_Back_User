package com.otd.otd_admin.application.admin.model;

import com.otd.configuration.enumcode.model.EnumChallengeRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminChallengePutReq {
    private Long cdId;
    private String cdName;
    private int cdGoal;
    private String cdImage;
    private int cdReward;
    private String cdType;
    private String cdUnit;
    private String note;
    private EnumChallengeRole tier;
    private int xp;
}

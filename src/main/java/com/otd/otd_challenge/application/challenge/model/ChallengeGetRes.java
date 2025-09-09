package com.otd.otd_challenge.application.challenge.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChallengeGetRes {
    private Long cdId;
    private int cdGoal;
    private String cdImage;
    private String cdName;
    private String cdPeriod;
    private int cdReward;
}

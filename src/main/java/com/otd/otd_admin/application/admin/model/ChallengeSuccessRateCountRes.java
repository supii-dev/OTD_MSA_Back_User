package com.otd.otd_admin.application.admin.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChallengeSuccessRateCountRes {
    private String challengeType;
    private Long totalCount;
    private Long successCount;
    private Long successRate;
}

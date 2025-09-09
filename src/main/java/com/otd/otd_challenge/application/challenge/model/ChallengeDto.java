package com.otd.otd_challenge.application.challenge.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChallengeDto {
    private List<ChallengeGetRes> dailyChallenges;
    private List<ChallengeGetRes> weeklyChallenges;
    private List<ChallengeGetRes> monthlyChallenges;
}

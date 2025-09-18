package com.otd.otd_challenge.application.challenge.model;

import com.otd.otd_challenge.entity.ChallengeDefinition;
import com.otd.otd_user.entity.User;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeHomeGetRes {
    private User user;
    private List<ChallengeProgressGetRes> personalChallenge;
    private List<ChallengeProgressGetRes> weeklyChallenge;
    private List<ChallengeProgressGetRes> competitionChallenge;

    private List<ChallengeDefinition> challengeDefinition;
}

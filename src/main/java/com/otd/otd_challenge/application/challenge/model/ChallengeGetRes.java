package com.otd.otd_challenge.application.challenge.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChallengeGetRes {
    private Long id;
    private int goal;
    private String image;
    private String name;
    private String period;
    private int reward;
}

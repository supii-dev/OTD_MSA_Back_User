package com.otd.otd_challenge.application.challenge.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChallengeGetRes {
    private Long id;
    private int goal;
    private String image;
    private String name;
    private String period;
    private int reward;
}

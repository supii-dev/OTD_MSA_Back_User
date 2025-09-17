package com.otd.otd_challenge.application.challenge.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChallengeRankGetRes {
    private String nickName;
    private String pic;
    private String totalRecord;
    private int rank;
}

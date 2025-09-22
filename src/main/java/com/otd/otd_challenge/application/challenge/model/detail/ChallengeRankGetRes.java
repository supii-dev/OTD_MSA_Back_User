package com.otd.otd_challenge.application.challenge.model.detail;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChallengeRankGetRes {
    private long userId;
    private String nickName;
    private String pic;
    private double totalRecord;
    private String formattedTotalRecord;
    private int rank;
}

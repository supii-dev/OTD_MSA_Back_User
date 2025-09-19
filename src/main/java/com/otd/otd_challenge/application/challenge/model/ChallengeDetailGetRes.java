package com.otd.otd_challenge.application.challenge.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@Setter
public class ChallengeDetailGetRes {
    private Long userId;
    private Long cdId;
    private String name;
    private int reward;
    private Double totalRecord;
    private Double goal;
    private Double percent;
    private int totalUsers;
    private int myRank;
    private List<ChallengeRankGetRes> ranking;
}

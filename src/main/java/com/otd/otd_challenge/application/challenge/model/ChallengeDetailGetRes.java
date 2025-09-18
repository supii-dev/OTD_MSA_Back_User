package com.otd.otd_challenge.application.challenge.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@Setter
public class ChallengeDetailGetRes {
    private long userId;
    private long cpId;
    private long cdId;
    private String name;
    private int reward;
    private double totalRecord;
    private double goal;
    private String formattedTotalRecord; // unit과 합체
//    private String formattedGoal; // unit과 합체
    private Double percent;
    private int totalUsers;
    private int myRank;
    private String unit;
    private List<ChallengeRankGetRes> topRanking;
    private List<ChallengeRankGetRes> aroundRanking;
    private boolean isSuccess;
}

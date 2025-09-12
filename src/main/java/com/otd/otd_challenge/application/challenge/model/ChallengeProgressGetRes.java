package com.otd.otd_challenge.application.challenge.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ChallengeProgressGetRes {
    private int userId;
    private int cpId;
    private int cdId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double record;
    private String image;
    private String period;
    private String name;
    private int reward;
}

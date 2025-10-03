package com.otd.otd_challenge.application.challenge.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ChallengeRecordDeleteReq {
    private Long userId;
    private Long recordId;
    private String name;
    private LocalDate recordDate;
    private LocalDate today;
    private int count;
}

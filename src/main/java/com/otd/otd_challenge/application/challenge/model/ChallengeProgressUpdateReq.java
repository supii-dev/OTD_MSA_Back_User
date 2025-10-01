package com.otd.otd_challenge.application.challenge.model;

import com.otd.configuration.util.FormattedTime;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ChallengeProgressUpdateReq {
    private Long userId;
    private String name;
    private Double record;
    private LocalDate recordDate;
}

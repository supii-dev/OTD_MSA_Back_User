package com.otd.otd_challenge.application.challenge.model.feignClient;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExerciseDataReq {
    private Long userId;
    private Long recordId;
    private String name;
    private Double record;
    private LocalDate recordDate;
    private LocalDate today;
    private int count;
    private int totalKcal;
}

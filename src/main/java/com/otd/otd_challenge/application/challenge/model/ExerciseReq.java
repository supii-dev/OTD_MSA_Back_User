package com.otd.otd_challenge.application.challenge.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class ExerciseReq {
    private Long userId;
    private LocalDate localDate;
}

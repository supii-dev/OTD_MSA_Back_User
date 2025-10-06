package com.otd.otd_challenge.application.challenge.model.feignClient;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MealDataReq {
    private Long userId;
    private LocalDate mealDay;
    private Double totalProtein;
    private String name;
    private LocalDate today;
}
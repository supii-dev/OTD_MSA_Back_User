package com.otd.otd_pointShop.application.stats.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthlyStatsRes {
    private String month;
    private Integer totalSpentPoints;
}

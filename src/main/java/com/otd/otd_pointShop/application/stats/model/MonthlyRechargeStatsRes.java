package com.otd.otd_pointShop.application.stats.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MonthlyRechargeStatsRes {
    private String month; // YYYY-MM
    private Long totalAmount; // 월별 총 충전 금액
}

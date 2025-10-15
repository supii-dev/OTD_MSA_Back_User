package com.otd.otd_pointShop.application.stats.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlySummaryStatsRes {
    private String month; // YYYY-MM
    private Long totalRecharge; // 충전 합계
    private Long totalPurchase; // 구매 합계
    private Long netChange; // 순잔액(충전 - 구매)
}

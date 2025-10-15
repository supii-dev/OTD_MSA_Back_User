package com.otd.otd_pointShop.application.stats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSpendTopRes {
    private Long userId; // 사용자 ID
    private String nickname; // 닉네임
    private Long totalAmount; // 총 금액(충전 or 구매)
    private String type; // recharge or purchase
}

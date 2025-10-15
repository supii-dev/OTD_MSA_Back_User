package com.otd.otd_pointShop.application.stats.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PopularItemRes {
    private String pointItemName; // 포인트 아이템명
    private int purchaseCount; // 구매 횟수
}

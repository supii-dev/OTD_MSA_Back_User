package com.otd.otd_pointShop.application.stats.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyDetailRes {
    private String type;       // "RECHARGE" or "PURCHASE"
    private String date;       // 거래일 (yyyy-MM-dd)
    private String nickname;   // 유저 닉네임
    private String itemName;   // 구매 아이템명 (충전은 null)
    private Long amount;       // 포인트 금액
    private String createdAt;  // createdAt → date와 동일 (프론트 호환용)
}
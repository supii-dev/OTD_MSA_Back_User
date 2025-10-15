package com.otd.otd_pointShop.application.purchase.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RechargePostReq {
    private Long adminId; // 관리자 전용 ID, 타인에게 충전 용도
    private Long userId; // 유저 ID
    private Integer amount; // 충전 금액
}

package com.otd.otd_pointShop.application.purchase.model;

import lombok.Data;

@Data
public class RechargePostReq {
    private Long userId; // 관리자 전용 ID, 타인에게 충전 용도
    private Integer amount; // 충전 포인트
}

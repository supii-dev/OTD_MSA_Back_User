package com.otd.otd_pointShop.application.purchase.model;

import com.otd.otd_pointShop.entity.PurchaseHistory;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RechargePostRes {
    private Long rechargeId; // 충전 이력 ID
    private Long adminId; // 관리자 ID
    private Long userId; // 유저 ID
    private String name; // 유저 이름
    private Integer amount; // 충전 금액
    private Integer balance; // 충전 후 잔액
    private LocalDateTime rechargeAt; // 충전 일시
}

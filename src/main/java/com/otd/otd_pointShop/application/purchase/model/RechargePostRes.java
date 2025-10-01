package com.otd.otd_pointShop.application.purchase.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RechargePostRes {
    private Long rechargeId;
    private Integer amount;
    private LocalDateTime rechargeTime;
}

package com.otd.otd_pointShop.application.purchase.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RechargeGetRes {
    private Long rechargeId;
    private Integer amount;
    private LocalDateTime rechargeTime;
}

package com.otd.otd_pointShop.application.purchase.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseHistoryRes {
    private Long purchaseId;
    private Long pointId;
    private String pointItemName;
    private int pointScore;
    private LocalDateTime purchaseTime;
}
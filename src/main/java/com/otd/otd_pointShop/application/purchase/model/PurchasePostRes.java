package com.otd.otd_pointShop.application.purchase.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PurchasePostRes {
    private Long purchaseId;
    private Long pointId;
    private String pointItemName;
    private Integer pointScore;
    private String pointItemImage;
    private LocalDateTime purchaseAt;
}

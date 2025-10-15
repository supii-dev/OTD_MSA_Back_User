package com.otd.otd_pointShop.application.purchase.model;

import com.otd.otd_pointShop.entity.PurchaseHistory;
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

    public static PurchasePostRes fromEntity(PurchaseHistory entity) {
        return PurchasePostRes.builder()
                .purchaseId(entity.getPurchaseId())
                .pointItemName(entity.getPoint().getPointItemName())
                .pointScore(entity.getPoint().getPointScore())
                .purchaseAt(entity.getPurchaseAt())
                .build();
    }
}

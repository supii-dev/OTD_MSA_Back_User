package com.otd.otd_pointShop.application.purchase.model;

import com.otd.otd_pointShop.entity.Point;
import com.otd.otd_pointShop.entity.PurchaseHistory;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseHistoryRes {
    private Long purchaseId;
    private Long pointId;
    private String pointItemName;
    private int pointScore;
    private LocalDateTime purchaseAt;
    private int userCurrentPoint;
    private String pointItemImage;

    public static PurchaseHistoryRes fromEntity(PurchaseHistory entity) {
        Point point = entity.getPoint();

        String imageUrl = (point.getPointItemImage() != null && !point.getPointItemImage().isEmpty())
                ? point.getPointItemImage().get(0).getImageUrl()
                : null;

        return PurchaseHistoryRes.builder()
                .purchaseId(entity.getPurchaseId())
                .pointId(point.getPointId())
                .pointItemName(point.getPointItemName())
                .pointScore(point.getPointScore())
                .pointItemImage(imageUrl)
                .purchaseAt(entity.getPurchaseAt())
                .build();
    }

}
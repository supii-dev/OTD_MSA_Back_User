package com.otd.otd_pointshop.application.purchase.model;

import com.otd.otd_pointshop.entity.PurchaseHistory;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePostRes {
    private Long purchaseId; // 구매 ID
    private Long pointId; // 상품 ID
    private String pointItemName; // 상품 이름
    private Integer pointScore; // 사용 포인트
    private String pointItemImage; // 상품 이미지 URL
    private LocalDateTime purchaseAt; // 구매 일시

    public static PurchasePostRes fromEntity(PurchaseHistory entity) {
        if (entity == null) return null;

        String imageUrl = null;
        if (entity.getPoint() != null &&
                entity.getPoint().getPointItemImages() != null &&
                !entity.getPoint().getPointItemImages().isEmpty()) {
            imageUrl = entity.getPoint().getPointItemImages().get(0).getImageUrl();
        }

        return PurchasePostRes.builder()
                .purchaseId(entity.getPurchaseId())
                .pointId(entity.getPoint() != null ? entity.getPoint().getPointId() : null)
                .pointItemName(entity.getPoint() != null ? entity.getPoint().getPointItemName() : "상품 정보 없음")
                .pointScore(entity.getPoint() != null ? entity.getPoint().getPointScore() : 0)
                .pointItemImage(imageUrl)
                .purchaseAt(entity.getPurchaseAt())
                .build();
    }
}
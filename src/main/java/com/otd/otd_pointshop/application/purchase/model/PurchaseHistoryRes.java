package com.otd.otd_pointshop.application.purchase.model;

import com.otd.otd_pointshop.entity.PurchaseHistory;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseHistoryRes {

    private Long purchaseId;          // 구매 내역 ID
    private Long userId;              // 사용자 ID
    private String userName;          // 사용자 이름
    private Long pointId;             // 포인트 상품 ID
    private String pointItemName;     // 상품명
    private int pointScore;           // 상품 포인트 가격
    private String pointItemImage;    // 상품 이미지 URL
    private LocalDateTime purchaseAt; // 구매 시각
    private Integer userCurrentPoint; // 구매 후 유저 잔여 포인트

    // 엔티티 → DTO 변환
    // null 안전 처리 > 이미지 URL 추출 포함
    public static PurchaseHistoryRes fromEntity(PurchaseHistory entity) {
        if (entity == null) return null;

        // 유저 및 포인트 존재 여부 확인
        Long userId = (entity.getUser() != null) ? entity.getUser().getUserId() : null;
        String userName = (entity.getUser() != null) ? entity.getUser().getName() : null;
        Integer currentPoint = (entity.getUser() != null) ? entity.getUser().getPoint() : null;

        Long pointId = (entity.getPoint() != null) ? entity.getPoint().getPointId() : null;
        String pointItemName = (entity.getPoint() != null) ? entity.getPoint().getPointItemName() : "상품 정보 없음";
        int pointScore = (entity.getPoint() != null) ? entity.getPoint().getPointScore() : 0;

        // 대표 이미지 추출
        String imageUrl = null;
        if (entity.getPoint() != null &&
                entity.getPoint().getPointItemImages() != null &&
                !entity.getPoint().getPointItemImages().isEmpty()) {
            imageUrl = entity.getPoint().getPointItemImages().get(0).getImageUrl();
        }

        return PurchaseHistoryRes.builder()
                .purchaseId(entity.getPurchaseId())
                .userId(userId)
                .userName(userName)
                .pointId(pointId)
                .pointItemName(pointItemName)
                .pointScore(pointScore)
                .pointItemImage(imageUrl)
                .purchaseAt(entity.getPurchaseAt())
                .userCurrentPoint(currentPoint)
                .build();
    }
}
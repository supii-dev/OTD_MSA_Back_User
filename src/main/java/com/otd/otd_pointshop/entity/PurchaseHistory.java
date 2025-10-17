package com.otd.otd_pointshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.otd.otd_user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "point_purchase_history")
public class PurchaseHistory {

    // 기본 키 (구매 이력 ID)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private Long purchaseId;

    // 구매자 정보
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_point_purchase_user")
    )
    @JsonIgnore
    private User user;

    // 구매한 포인트 아이템
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "point_id",
            referencedColumnName = "point_id",
            foreignKey = @ForeignKey(name = "fk_point_purchase_item")
    )
    private Point point;

    // 사용/구매 구분 (ITEM_PURCHASE / GENERAL_USE)
    @Column(name = "usage_type", nullable = false, length = 30)
    private String usageType;

    // 구매 또는 사용 시각
    @CreationTimestamp
    @Column(name = "purchase_at", nullable = false, updatable = false)
    private LocalDateTime purchaseAt;

    // entity 생성 시 기본값
    @PrePersist
    public void prePersist() {
        if (this.usageType == null) {
            this.usageType = "ITEM_PURCHASE";
        }
        if (this.purchaseAt == null) {
            this.purchaseAt = LocalDateTime.now();
        }
    }

    // equals / hashCode (영속성 ID 기반)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PurchaseHistory)) return false;
        PurchaseHistory that = (PurchaseHistory) o;
        return purchaseId != null && purchaseId.equals(that.getPurchaseId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
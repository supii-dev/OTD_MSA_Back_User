package com.otd.otd_pointShop.entity;

import com.otd.otd_user.entity.User;
import jakarta.persistence.*;
import jdk.jfr.Enabled;
import lombok.Data;

import java.time.LocalDateTime;

@Enabled
@Data
@Entity
@Table(name = "point_purchase_history")
public class PurchaseHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName ="user_id", nullable = false
                , foreignKey = @ForeignKey(name="fk_point_user")) // FK
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id", nullable = false)
    private Point point;

    private LocalDateTime purchaseAt;

    @PrePersist
    public void prePersist() {
        this.purchaseAt = LocalDateTime.now();
    }
}

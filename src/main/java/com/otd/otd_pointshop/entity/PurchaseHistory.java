package com.otd.otd_pointshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false
            , foreignKey = @ForeignKey(name = "fk_point_user")) // FK
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "point_id", nullable = false)
    private Point point;

    @Column(name = "purchase_at")
    private LocalDateTime purchaseTime;

    @PrePersist
    public void prePersist() {
        this.purchaseTime = LocalDateTime.now();
    }
}
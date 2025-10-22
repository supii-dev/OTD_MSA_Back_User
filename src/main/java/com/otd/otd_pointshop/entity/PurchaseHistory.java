package com.otd.otd_pointshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.otd.otd_user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_purchase_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName ="user_id", nullable = false
                , foreignKey = @ForeignKey(name="fk_point_user")) // FK
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "point_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Point point;

    @Column(name= "usage_type", nullable = false)
    private String usageType;

    @Column(name = "purchase_at")
    private LocalDateTime purchaseAt;

    @PrePersist
    public void prePersist() {
        if (this.purchaseAt == null) {
            this.purchaseAt = LocalDateTime.now();
        }
        if (this.usageType == null) {
            this.usageType = "GENERAL_USE";
        }
    }
}
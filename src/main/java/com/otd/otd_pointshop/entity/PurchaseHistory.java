package com.otd.otd_pointshop.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.otd.otd_user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

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

    // 쿠폰 사용 여부 (기본 false)
    @Column(name = "is_used", nullable = false)
    private boolean isUsed = false;

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        this.isUsed = used;
    }
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    
    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }
}
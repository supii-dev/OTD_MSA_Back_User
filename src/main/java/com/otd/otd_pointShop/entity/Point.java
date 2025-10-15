package com.otd.otd_pointShop.entity;

import com.otd.otd_user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "pointCategory", "pointItemImages"})
@Entity
@Table(name = "point")
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long pointId; // 고유 ID

    // 상품 기본 정보
    @Column(nullable = false)
    private Integer pointScore;

    @Column(nullable = false, length = 100)
    private String pointItemName;

    @Column(length = 500)
    private String pointItemContent;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 유저 매핑 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id",
            foreignKey = @ForeignKey(name="fk_point_user")) // FK
    private User user;

    // 유저 현재 포인트
    @Transient
    private int userCurrentPoint;

    public void syncUserPoint() {
        if (this.user != null) this.userCurrentPoint = this.user.getPoint();
    }

    // 카테고리 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_category_id", referencedColumnName = "point_category_id")
    private PointCategory pointCategory;

    // 상품 이미지 목록
    @OneToMany(mappedBy = "point", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PointImage> pointItemImage = new ArrayList<>();
}

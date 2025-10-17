package com.otd.otd_pointshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.otd.otd_user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//포인트샵 상품(Point) 엔티티
// 유저, 카테고리, 이미지 연관관계 포함

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "pointCategory", "pointItemImages"})
@Entity
@Table(name = "point")
public class Point {

    // 고유 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long pointId;

    // 상품명
    @Column(name = "point_item_name", nullable = false, length = 100)
    private String pointItemName;

    // 상품 설명
    @Column(name = "point_item_content", length = 500)
    private String pointItemContent;

    // 필요 포인트 (상품 가격)
    @Column(name = "point_score", nullable = false)
    private Integer pointScore;

    // 생성일
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 수정일
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 🔹 관계 매핑
    // 등록한 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id",
            foreignKey = @ForeignKey(name = "fk_point_user")
    )
    @JsonIgnore
    private User user;

    // 카테고리 (ex: 기프티콘 / 음료 / 상품권 등)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "point_category_id",
            referencedColumnName = "point_category_id",
            foreignKey = @ForeignKey(name = "fk_point_category")
    )
    @JsonIgnore
    private PointCategory pointCategory;

    // 상품 이미지 목록
    @OneToMany(mappedBy = "point", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PointImage> pointItemImages = new ArrayList<>();

    // 🔹 유틸 필드, 헬퍼 메서드
    // 현재 사용자 포인트 (DB에 저장되지 않음)
    @Transient
    private int userCurrentPoint;

    public void syncUserPoint() {
        if (this.user != null) {
            this.userCurrentPoint = this.user.getPoint();
        }
    }

    // 상품 이미지 추가 시 양방향 관계 자동 설정
    public void addImage(PointImage image) {
        if (this.pointItemImages == null) this.pointItemImages = new ArrayList<>();
        this.pointItemImages.add(image);
        image.setPoint(this);
    }

    public void setImages(List<PointImage> images) {
        this.pointItemImages.clear();
        if (images != null) {
            images.forEach(this::addImage);
        }
    }
}
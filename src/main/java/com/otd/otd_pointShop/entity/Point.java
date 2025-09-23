package com.otd.otd_pointShop.entity;

import com.otd.otd_user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "point_item")
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId; // 고유 ID

    @Column(nullable = false)
    private int pointScore;

    @Column(nullable = false, length = 100)
    private String pointItemName;

    @Column(length = 500)
    private String pointItemContent;

    @Column(length = 255)
    private String pointItemImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // FK
    private User user;

    @ManyToOne
    @JoinColumn(name = "point_category_id")
    private PointCategory pointCategory;

    @OneToMany(mappedBy = "point", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PointImage> images = new ArrayList<>();
}

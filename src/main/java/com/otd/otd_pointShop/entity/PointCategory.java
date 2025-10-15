package com.otd.otd_pointShop.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "point_category")
public class PointCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_category_id")
    private Long pointCategoryId;

    private String categoryName;

    @OneToMany(mappedBy = "pointCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Point> items = new ArrayList<>();
}

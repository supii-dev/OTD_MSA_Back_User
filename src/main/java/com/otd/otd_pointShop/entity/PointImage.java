package com.otd.otd_pointShop.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "point_image")
public class PointImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Column(nullable = false, length = 255)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id", nullable = false)
    private Point point;
}

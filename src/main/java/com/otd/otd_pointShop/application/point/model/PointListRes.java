package com.otd.otd_pointShop.application.point.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointListRes {
    private Long pointId;
    private String pointItemName;
    private String pointItemImage;
    private int pointScore;
    private LocalDateTime createdAt;
}

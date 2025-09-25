package com.otd.otd_pointShop.application.point.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointListRes {
    private Integer pointId;
    private String pointItemName;
    private String pointItemContent;
    private String pointItemImage;
    private Integer pointScore;
    private LocalDateTime createdAt;
}

package com.otd.otd_pointShop.application.point.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class PointGetRes {
    private Integer pointId;
    private Integer pointScore;
    private String pointItemName;
    private String pointItemContent;
    private LocalDateTime createdAt;
    private List<String> images;
}

package com.otd.otd_pointShop.application.point.model;

import lombok.Data;

@Data
public class PointDto {
    private Integer userId;
    private Integer pointId;
    private Integer pointScore;
    private String pointItemName;
    private String pointItemContent;
    private String pointItemImage;
}

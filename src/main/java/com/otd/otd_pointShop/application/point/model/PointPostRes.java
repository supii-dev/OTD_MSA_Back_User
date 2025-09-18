package com.otd.otd_pointShop.application.point.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PointPostRes {
    private int userId;
    private int pointId;
    private String pointItemName;
    private String pointItemContent;
    private String pointItemImage;
}

package com.otd.otd_pointShop.application.point.model;

import lombok.Data;

@Data
public class PointGetRes {
    private int userId;
    private int pointId;
    private int pointScore;
    private String pointItemName;
    private String pointItemContent;
    private String pointItemImage;
}

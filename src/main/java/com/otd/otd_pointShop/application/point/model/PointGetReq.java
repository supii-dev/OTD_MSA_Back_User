package com.otd.otd_pointShop.application.point.model;

import lombok.Data;

@Data
public class PointGetReq {
    private int pointId;
    private String pointItemName;
    private String pointItemContent;
    private String pointItemImage;
}

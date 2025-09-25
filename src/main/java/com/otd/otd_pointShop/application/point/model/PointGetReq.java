package com.otd.otd_pointShop.application.point.model;

import lombok.Data;

@Data
public class PointGetReq {
    private Long userId;
    private Integer pointId;
    private Integer pointScore;
    private String pointItemName;
    private String pointItemContent;
    private String pointItemImage;
    private Integer offset;
    private Integer pageSize;
}

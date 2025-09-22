package com.otd.otd_pointShop.application.point.model;

import lombok.Data;

@Data
public class PointPostReq {
    private Long pointId;
    private Integer pointScore;
    private String pointItemName;
    private String pointItemContent;
}

package com.otd.otd_pointShop.application.point.model;

import lombok.Data;

import java.util.List;

@Data
public class PointPostReq {
    private Integer pointScore;
    private String pointItemName;
    private String pointItemContent;
    private List<String> images;
}

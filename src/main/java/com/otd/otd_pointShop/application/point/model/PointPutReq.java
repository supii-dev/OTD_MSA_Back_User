package com.otd.otd_pointShop.application.point.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PointPutReq {
    private Long pointId;
    private Long pointScore;
    private String pointItemName;
    private String pointItemContent;
    private List<String> images;
}

package com.otd.otd_pointShop.application.point.model;

import lombok.Data;
import java.util.List;

@Data
public class PointGetReq {
    private Long userId;
    private Long pointId;
    private String pointItemName;
    private String pointItemContent;
}

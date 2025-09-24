package com.otd.otd_pointShop.application.point.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PointPutReq {
    private Long pointId;
    private Integer pointScore;
    private String pointItemName;
    private String pointItemContent;
}

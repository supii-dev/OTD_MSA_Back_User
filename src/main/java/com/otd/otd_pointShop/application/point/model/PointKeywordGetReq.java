package com.otd.otd_pointShop.application.point.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PointKeywordGetReq {
    @Size(min = 1)
    private String keyword;
    private Integer page = 0;
    private Integer size = 10;
}

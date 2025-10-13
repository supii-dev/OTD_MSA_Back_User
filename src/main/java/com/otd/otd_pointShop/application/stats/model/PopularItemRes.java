package com.otd.otd_pointShop.application.stats.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PopularItemRes {
    private String itemName;
    private Integer purchaseCount;
}

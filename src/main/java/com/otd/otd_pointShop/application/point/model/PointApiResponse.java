package com.otd.otd_pointShop.application.point.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PointApiResponse<T> {
    private boolean success;
    private T data;
}

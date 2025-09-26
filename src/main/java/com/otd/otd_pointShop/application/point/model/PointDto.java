package com.otd.otd_pointShop.application.point.model;

import com.otd.configuration.model.JwtUser;
import lombok.Data;

@Data
public class PointDto {
    private PointGetRes pointGetRes;
    private JwtUser jwtUser;
}

package com.otd.otd_pointShop.application.point.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class PointApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    // 생성자 1: 메시지만 포함
    public PointApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // 생성자 2: 데이터와 메시지 포함
    public PointApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // 생성자 3: 데이터만 포함
    public PointApiResponse(boolean success, T data) {
        this.success = success;
        this.data = data;
    }
}
package com.otd.otd_pointShop.application.point.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class PointApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Integer userCurrentPoint;

    // 생성자 1: 메시지만 포함
    public PointApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // 생성자 2: 데이터만 포함
    public PointApiResponse(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    // 생성자 2: 메시지, 데이터 포함
    public PointApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // 생성자 3: 데이터와 메시지 포함
    public PointApiResponse(boolean success, String message, T data, Integer userCurrentPoint) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.userCurrentPoint = userCurrentPoint;
    }

    // 성공 응답 (기본 메시지)
    public static <T> PointApiResponse<T> success(T data) {
        return new PointApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", data);
    }

    // 성공 응답 (사용자 정의 메시지 포함)
    public static <T> PointApiResponse<T> success(String message, T data) {
        return new PointApiResponse<>(true, message, data);
    }

    // 실패 응답
    public static <T> PointApiResponse<T> error(String message) {
        return new PointApiResponse<>(false, message, null);
    }
}
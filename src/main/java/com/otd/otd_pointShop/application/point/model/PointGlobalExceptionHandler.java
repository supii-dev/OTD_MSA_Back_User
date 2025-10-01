package com.otd.otd_pointShop.application.point.model;

import com.otd.otd_pointShop.application.point.model.PointApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.otd.otd_pointShop")
public class PointGlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<PointApiResponse<?>> handleException(Exception e) {
        return ResponseEntity.status(500)
                .body(new PointApiResponse<>(false, "서버 오류: " + e.getMessage()));
    }
}

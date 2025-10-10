package com.otd.otd_pointShop.application.purchase;

public interface PointBalanceService {
    Integer getPointBalance(Long userId); // 포인트 조회
    void pointIncrement(Long userId, int amount); // 포인트 적립
    void pointDecrement(Long userId, int amount); // 포인트 차감
}

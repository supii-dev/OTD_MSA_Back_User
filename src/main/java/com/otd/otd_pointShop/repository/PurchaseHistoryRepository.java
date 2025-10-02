package com.otd.otd_pointShop.repository;

import com.otd.otd_pointShop.entity.Point;
import com.otd.otd_pointShop.entity.PurchaseHistory;
import com.otd.otd_user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory,Long> {
    List<PurchaseHistory> findByUser(User user);
    List<PurchaseHistory> findByUser_UserId(Long userId);

    // 사용자별 총 사용 포인트 상위 10명
    @Query("SELECT p.user.userId, SUM(p.point.pointScore) " +
            "FROM PurchaseHistory p " +
            "GROUP BY p.user.userId " +
            "ORDER BY SUM(p.point.pointScore) DESC")
    List<Object[]> findTopUsersByTotalSpentPoints();

    // 월별 포인트 사용량
    @Query("SELECT FUNCTION('DATE_FORMAT', p.purchaseTime, '%Y-%m') AS month, SUM(p.point.pointScore) " +
            "FROM PurchaseHistory p " +
            "GROUP BY FUNCTION('DATE_FORMAT', p.purchaseTime, '%Y-%m') " +
            "ORDER BY month ASC")
    List<Object[]> findMonthlySpentPoints();

    // 인기 상품 상위 10개
    @Query("SELECT p.point.pointItemName, COUNT(p.point.pointId) AS purchaseCount " +
            "FROM PurchaseHistory p " +
            "GROUP BY p.point.pointItemName " +
            "ORDER BY purchaseCount DESC")
    List<Object[]> findTopPurchasedItems();

    List<PurchaseHistory> findByPoint(Point point);
    List<PurchaseHistory> findByPurchaseTime(LocalDateTime purchaseTime);
    List<PurchaseHistory> findByPurchaseTimeBetween(LocalDateTime start, LocalDateTime end);
}

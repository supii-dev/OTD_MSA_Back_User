package com.otd.otd_pointShop.repository;

import com.otd.otd_pointShop.entity.Point;
import com.otd.otd_pointShop.entity.PurchaseHistory;
import com.otd.otd_user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory,Long> {
    List<PurchaseHistory> findByUser(User user);
    List<PurchaseHistory> findByPoint(Point point);
    List<PurchaseHistory> findByPurchaseTime(LocalDateTime purchaseTime);
    List<PurchaseHistory> findByPurchaseTimeBetween(LocalDateTime start, LocalDateTime end);
}

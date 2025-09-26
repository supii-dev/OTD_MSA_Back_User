package com.otd.otd_pointShop.repository;

import com.otd.otd_pointShop.entity.PurchaseHistory;
import com.otd.otd_user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory,Long> {
    List<PurchaseHistory> findByUser_UserId(User user);
}

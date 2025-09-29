package com.otd.otd_pointShop.repository;

import com.otd.otd_pointShop.entity.RechargeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RechargeHistoryRepository extends JpaRepository<RechargeHistory, Long> {
    List<RechargeHistory> findAllByUser_UserId(Long userId);
    List<RechargeHistory> findByUser_UserId(Long userId);
}

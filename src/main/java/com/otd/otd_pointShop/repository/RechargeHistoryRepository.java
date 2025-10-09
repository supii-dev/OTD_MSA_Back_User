package com.otd.otd_pointShop.repository;

import com.otd.otd_pointShop.entity.RechargeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RechargeHistoryRepository extends JpaRepository<RechargeHistory, Long> {
    List<RechargeHistory> findByUser_UserId(Long userId);

    // 사용자별 충전 포인트 총합
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM RechargeHistory r WHERE r.user.userId = :userId")
    Optional<Integer> findTotalRechargeByUserId(Long userId);
}

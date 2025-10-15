package com.otd.otd_pointShop.repository;

import com.otd.otd_pointShop.entity.PurchaseHistory;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<PurchaseHistory, Long> {

    // 월별 충전 합계
    @Query(value = """
        SELECT DATE_FORMAT(r.recharge_at, '%Y-%m') AS month, SUM(r.amount)
        FROM recharge_history r
        GROUP BY DATE_FORMAT(r.recharge_at, '%Y-%m')
        ORDER BY month ASC
    """, nativeQuery = true)
    List<Object[]> findMonthlyRechargeTotals();

    // 월별 구매 합계
    @Query(value = """
        SELECT DATE_FORMAT(p.purchase_time, '%Y-%m') AS month, SUM(p.point_score)
        FROM purchase_history p
        GROUP BY DATE_FORMAT(p.purchase_time, '%Y-%m')
        ORDER BY month ASC
    """, nativeQuery = true)
    List<Object[]> findMonthlyPurchaseTotals();

    // 특정 유저 월별 충전 합계
    @Query(value = """
        SELECT DATE_FORMAT(r.recharge_at, '%Y-%m') AS month, SUM(r.amount)
        FROM recharge_history r
        WHERE r.user_id = :userId
        GROUP BY DATE_FORMAT(r.recharge_at, '%Y-%m')
        ORDER BY month ASC
    """, nativeQuery = true)
    List<Object[]> findMonthlyRechargeTotalsByUser(@Param("userId") Long userId);

    // 특정 유저 월별 구매 합계
    @Query(value = """
        SELECT DATE_FORMAT(p.purchase_time, '%Y-%m') AS month, SUM(p.point_score)
        FROM purchase_history p
        WHERE p.user_id = :userId
        GROUP BY DATE_FORMAT(p.purchase_time, '%Y-%m')
        ORDER BY month ASC
    """, nativeQuery = true)
    List<Object[]> findMonthlyPurchaseTotalsByUser(@Param("userId") Long userId);

    // 월별 상세 조회 (충전)
    @Query(value = """
        SELECT DATE_FORMAT(r.recharge_at, '%Y-%m-%d') AS date,
               u.nickname,
               r.amount
        FROM recharge_history r
        JOIN user u ON r.user_id = u.user_id
        WHERE DATE_FORMAT(r.recharge_at, '%Y-%m') = :month
        ORDER BY r.recharge_at DESC
    """, nativeQuery = true)
    List<Object[]> findRechargeDetailsByMonth(@Param("month") String month);

    // 월별 상세 조회 (충전, 유저별)
    @Query(value = """
        SELECT DATE_FORMAT(r.recharge_at, '%Y-%m-%d') AS date,
               u.nickname,
               r.amount
        FROM recharge_history r
        JOIN user u ON r.user_id = u.user_id
        WHERE DATE_FORMAT(r.recharge_at, '%Y-%m') = :month
          AND r.user_id = :userId
        ORDER BY r.recharge_at DESC
    """, nativeQuery = true)
    List<Object[]> findRechargeDetailsByMonthAndUser(@Param("month") String month, @Param("userId") Long userId);

    // 월별 상세 조회 (구매)
    @Query(value = """
        SELECT DATE_FORMAT(p.purchase_time, '%Y-%m-%d') AS date,
               u.nickname,
               pr.point_item_name AS itemName,
               p.point_score AS amount
        FROM purchase_history p
        JOIN user u ON p.user_id = u.user_id
        JOIN point pr ON p.point_id = pr.point_id
        WHERE DATE_FORMAT(p.purchase_time, '%Y-%m') = :month
        ORDER BY p.purchase_time DESC
    """, nativeQuery = true)
    List<Object[]> findPurchaseDetailsByMonth(@Param("month") String month);

    // 월별 상세 조회 (구매, 유저별)
    @Query(value = """
        SELECT DATE_FORMAT(p.purchase_time, '%Y-%m-%d') AS date,
               u.nickname,
               pr.point_item_name AS itemName,
               p.point_score AS amount
        FROM purchase_history p
        JOIN user u ON p.user_id = u.user_id
        JOIN point pr ON p.point_id = pr.point_id
        WHERE DATE_FORMAT(p.purchase_time, '%Y-%m') = :month
          AND p.user_id = :userId
        ORDER BY p.purchase_time DESC
    """, nativeQuery = true)
    List<Object[]> findPurchaseDetailsByMonthAndUser(@Param("month") String month, @Param("userId") Long userId);

    // TOP 10 충전자
    @Query(value = """
        SELECT u.user_id, u.nickname, SUM(r.amount) AS totalRecharge
        FROM recharge_history r
        JOIN user u ON r.user_id = u.user_id
        GROUP BY u.user_id, u.nickname
        ORDER BY totalRecharge DESC
        LIMIT 10
    """, nativeQuery = true)
    List<Object[]> findTopRechargeUsers();

    // TOP 10 구매자
    @Query(value = """
        SELECT u.user_id, u.nickname, SUM(p.point_score)
        FROM purchase_history p
        JOIN user u ON p.user_id = u.user_id
        GROUP BY u.user_id, u.nickname
        ORDER BY SUM(p.point_score) DESC
        LIMIT 10
    """, nativeQuery = true)
    List<Object[]> findTopPurchaseUsers();

    // 인기 아이템 (상품명, 구매횟수)
    @Query(value = """
        SELECT pr.point_item_name, COUNT(*)
        FROM purchase_history p
        JOIN point pr ON p.point_id = pr.point_id
        GROUP BY pr.point_item_name
        ORDER BY COUNT(*) DESC
        LIMIT 10
    """, nativeQuery = true)
    List<Object[]> findPopularItems();

    // 사용 포인트 상위 10 유저
    @Query(value = """
        SELECT u.user_id, SUM(p.point_score)
        FROM purchase_history p
        JOIN user u ON p.user_id = u.user_id
        GROUP BY u.user_id
        ORDER BY SUM(p.point_score) DESC
        LIMIT 10
    """, nativeQuery = true)
    List<Object[]> findTopUsersByTotalSpentPoints();
}
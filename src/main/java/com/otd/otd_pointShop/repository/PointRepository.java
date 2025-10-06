package com.otd.otd_pointShop.repository;

import com.otd.otd_pointShop.entity.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point,Long> {
    Page<Point> findByUser_UserId(Long userId, Pageable pageable); // 유저 ID 조회
    Page<Point> findByUser_UserIdAndPointItemContentContaining(Long userId, String keyword, Pageable pageable);
    Optional<Point> findById(Long pointId);
}

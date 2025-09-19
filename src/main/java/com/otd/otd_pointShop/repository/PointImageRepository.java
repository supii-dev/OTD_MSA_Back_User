package com.otd.otd_pointShop.repository;

import com.otd.otd_pointShop.entity.Point;
import com.otd.otd_pointShop.entity.PointImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PointImageRepository extends JpaRepository<PointImage, Long> {
    List<PointImage> findByPoint(Point point); // 특정 포인트 아이템 이미지 목록 조회
    List<PointImage> findByPoint_PointId(Long pointId);
}

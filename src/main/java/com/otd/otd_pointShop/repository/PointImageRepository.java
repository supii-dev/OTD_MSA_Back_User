package com.otd.otd_pointShop.repository;

import com.otd.otd_pointShop.entity.Point;
import com.otd.otd_pointShop.entity.PointImage;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface PointImageRepository extends JpaRepository<PointImage, Long> {
    Page<PointImage> findByPoint_PointId(Long pointId, Pageable pageable);
    List<PointImage> findByPoint(Point point); // 특정 포인트 아이템 이미지 목록 조회
    List<PointImage> findByPoint_PointImage(Long pointId, String pointImage);
    void updateAllByPoint(Point point);
    void deleteAllByPoint(Point point);
}

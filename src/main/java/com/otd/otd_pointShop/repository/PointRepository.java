package com.otd.otd_pointShop.repository;

import com.otd.otd_pointShop.entity.Point;
import com.otd.otd_pointShop.entity.PointImage;
import com.otd.otd_user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointRepository extends JpaRepository<Point,Long> {
    List<Point> findByUser(User user); // 특정 유저 포인트 아이템 조회
    List<Point> findByUser_UserId(Long UserId); // 유저 ID 조회
    List<Point> findByPointItemNameContaining(String keyword); // 키워드 검색
    List<Point> save(List<Point> point);
    List<Point> updateByPoint_PointId(Long pointId);
    List<Point> deleteByPoint_PointId(Long pointId);
    List<PointImage> findByPoint_PointId(Long pointId);
    List<Point> findByUserWithPagination(Long userId,int offset,int pageSize);
}

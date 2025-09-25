package com.otd.otd_pointShop.repository;

import com.otd.otd_pointShop.entity.Point;
import com.otd.otd_pointShop.entity.PointImage;
import com.otd.otd_user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point,Integer> {
    List<Point> findByUser(User user); // 특정 유저 포인트 아이템 조회
    List<Point> findByUser_UserId(Long userId); // 유저 ID 조회
    List<Point> findByPointItemNameContaining(String keyword); // 키워드 검색
    Optional<Point> findById(Integer pointId);
    List<Point> save(List<Point> point);
    List<Point> updateByPoint_PointId(Integer pointId);
    List<Point> deleteByPoint_PointId(Integer pointId);
    List<PointImage> findByPoint_PointId(Integer pointId);
    List<Point> findByUserWithPagination(Long userId,Integer offset,Integer pageSize);
}

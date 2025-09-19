package com.otd.otd_pointShop.repository;

import com.otd.otd_pointShop.entity.Point;
import com.otd.otd_user.entity.User;

import java.util.List;

public interface PointRepository {
    List<Point> findByUser(User user); // 특정 유저 포인트 아이템 조회
    List<Point> findByUser_UserId(Long UserId); // 유저 ID 조회
    List<Point> findByPointItemNameContaining(String keyword); // 키워드 검색
    List<Point> save(List<Point> point);
}

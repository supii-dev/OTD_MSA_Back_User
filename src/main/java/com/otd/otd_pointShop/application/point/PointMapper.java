package com.otd.otd_pointShop.application.point;

import com.otd.otd_pointShop.entity.Point;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PointMapper {
    // 유저별 포인트 목록 조회
    List<Point> pointFindAll(
        @Param("userId") Long userId,
        @Param("offset") int offset,
        @Param("pageSize") int pageSize
    );

    Point pointFindOne(
        @Param("userId") Long userId,
        @Param("pointId") Long pointId
    );

    int savePoint(Point point);
    int updatePoint(Point point);
    int deletePoint(
        @Param("userId") Long userId,
        @Param("pointId") Long pointId
    );
}

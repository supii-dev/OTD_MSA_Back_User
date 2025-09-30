package com.otd.otd_pointShop.application.point;

import com.otd.otd_pointShop.entity.Point;
import com.otd.otd_pointShop.entity.PointImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PointshopMapper {
    // 유저별 포인트 목록 조회
    List<Point> findAllByUserId(
        @Param("userId") Long userId,
        @Param("offset") Integer offset,
        @Param("pageSize") Integer pageSize
    );

    Point findOneByUserIdAndPointId(
        @Param("userId") Long userId,
        @Param("pointId") Long pointId
    );

    List<PointImage> findImagesByPointId(
            @Param("pointId") Long pointId);

    int savePoint(Point point);
    int updatePoint(Point point);
    int deletePoint(
        @Param("userId") Long userId,
        @Param("pointId") Long pointId
    );
}

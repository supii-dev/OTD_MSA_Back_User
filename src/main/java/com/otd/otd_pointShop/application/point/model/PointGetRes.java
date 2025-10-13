package com.otd.otd_pointShop.application.point.model;

import com.otd.otd_pointShop.entity.PointImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointGetRes {
    private Long pointId;
    private Integer pointScore;
    private String pointItemName;
    private String pointItemContent;

    // 대표 image list (pointImage 참조)
    private List<PointImage> pointItemImages;

    // 이미지 응답 DTO
    private List<PointImageRes> images;
    private LocalDateTime createdAt;

}

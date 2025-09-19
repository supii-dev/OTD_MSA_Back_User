package com.otd.otd_pointShop.application.point;

import com.otd.otd_pointShop.application.point.model.PointPostReq;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pointshop")
public class PointController {
    private final PointService pointService;

    @PostMapping(value = "/add, consumes = MediaType.MULTIPART_FORM_DATA_VALUE")
    public ResponseEntity<?> addPointItem(
            @RequestPart("data") PointPostReq dto,
            @RequestPart(value = "images", required = false) Multipartfile[] images,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        pointService.createPointItem(dto, images, userId);
        return ResponseEntity.ok().build();
    }
}

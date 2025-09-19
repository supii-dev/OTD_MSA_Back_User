package com.otd.otd_pointShop.application.point;

import com.otd.otd_pointShop.application.point.model.PointPostReq;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pointshop")
public class PointController {
    private final PointService pointService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addPointItem(
            @RequestPart("data") PointPostReq dto,
            @RequestPart(value = "images", required = false) MultipartFile[] images,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");

        if(userId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        pointService.createPointItem(dto, images, userId);
        return ResponseEntity.ok("포인트 아이템 등록 완료");
    }
}

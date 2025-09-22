package com.otd.otd_pointShop.application.point;

import com.otd.otd_pointShop.application.point.model.PointApiResponse;
import com.otd.otd_pointShop.application.point.model.PointPostReq;
import com.otd.otd_pointShop.application.point.model.PointPutReq;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        try {
            pointService.createPointItem(dto, images, userId);
            return ResponseEntity.ok(new PointApiResponse<>(true, "포인트 아이템 등록 완료"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new PointApiResponse<>(false, "서버 오류: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePointItem(
            @RequestPart("data") PointPutReq dto,
            @RequestPart(value = "images", required = false) MultipartFile[] images,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            pointService.updatePointItem(dto, images, userId);
            return ResponseEntity.ok(new PointApiResponse<>(true, "포인트 수정 완료"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new PointApiResponse<>(false, "서버 오류: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePointItem(
            @RequestBody PointPostReq dto,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        try {
            pointService.deletePointItem(dto.getPointId(), userId);
            return ResponseEntity.ok("포인트가 제거되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new PointApiResponse<>(false, "삭제에 실패하였습니다." + e.getMessage()));
        }
    }
}
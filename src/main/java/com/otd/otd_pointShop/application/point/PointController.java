package com.otd.otd_pointShop.application.point;

import com.otd.otd_pointShop.application.point.model.PointApiResponse;
import com.otd.otd_pointShop.application.point.model.PointPostReq;
import com.otd.otd_pointShop.application.point.model.PointPutReq;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
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
            log.info("[포인트 등록] 사용자 ID: {}, 제목: {}", userId, dto.getPointItemName());
            return ResponseEntity.ok(new PointApiResponse<>(true, "포인트 아이템 등록 완료"));
        } catch (Exception e) {
            log.error("사용자 {}가 포인트를 등록하는 데 실패했습니다. 제목: {}", userId, dto.getPointItemName());
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
            log.info("사용자 {}가 포인트를 수정했습니다. point ID: {}", userId, dto.getPointItemName());
            return ResponseEntity.ok(new PointApiResponse<>(true, "포인트 수정 완료"));
        } catch (Exception e) {
            log.error("[포인트 수정 실패] 사용자 ID: {}, 제목: {}", userId, dto.getPointItemName(), e);
            return ResponseEntity.status(500).body(new PointApiResponse<>(false, "서버 오류: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePointItem(
            @RequestParam("pointId") Long pointId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if(userId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        try {
            pointService.deletePointItem(pointId, userId);
            log.info("[포인트 삭제] 사용자 ID: {}, pointId: {}", userId, pointId);
            return ResponseEntity.ok(new PointApiResponse<>(true, "point ID " + pointId + "가 제거되었습니다."));
        } catch (Exception e) {
            log.error("Point 삭제 중 예외 발생", e);
            return ResponseEntity.status(500).body(new PointApiResponse<>(false, "서버 내부 오류로 삭제에 실패하였습니다."));
        }
    }
}
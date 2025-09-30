package com.otd.otd_pointShop.application.point;

import com.otd.configuration.model.ResultResponse;
import com.otd.configuration.model.UserPrincipal;
import com.otd.otd_pointShop.application.point.model.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pointshop")
public class PointshopController {
    private final PointshopService pointshopService;

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
            pointshopService.createPointItem(dto, images, userId);
            log.info("[포인트 등록] 사용자 ID: {}, 제목: {}", userId, dto.getPointItemName());
            return ResponseEntity.ok(new PointApiResponse<>(true, "포인트 아이템 등록 완료"));
        } catch (Exception e) {
            log.error("사용자 {}가 포인트를 등록하는 데 실패했습니다. 제목: {}", userId, dto.getPointItemName());
            return ResponseEntity.status(500).body(new PointApiResponse<>(false, "서버 오류: " + e.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getPointList (
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        if(userPrincipal == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        List<PointListRes> list = pointshopService.getPointListByUser(userPrincipal.getSignedUserId(), pageable);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/keyword")
    public ResultResponse<?> getPointKeywordByUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "") String keyword,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        if(userPrincipal == null) {
            return new ResultResponse<>("로그인이 필요합니다.", null);
        }
        Set<String> result = pointshopService.getPointKeywordByUser(
                userPrincipal.getSignedUserId(), keyword, pageable);
        return new ResultResponse<>(String.format("rows: %d", result.size()), result);
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
            pointshopService.updatePointItem(dto, images, userId);
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
            pointshopService.deletePointItem(pointId, userId);
            log.info("[포인트 삭제] 사용자 ID: {}, pointId: {}", userId, pointId);
            return ResponseEntity.ok(new PointApiResponse<>(true, "point ID " + pointId + "가 제거되었습니다."));
        } catch (Exception e) {
            log.error("Point 삭제 중 예외 발생", e);
            return ResponseEntity.status(500).body(new PointApiResponse<>(false, "서버 내부 오류로 삭제에 실패하였습니다."));
        }
    }
}
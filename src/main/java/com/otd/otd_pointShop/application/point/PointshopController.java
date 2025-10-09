package com.otd.otd_pointShop.application.point;

import com.otd.configuration.model.ResultResponse;
import com.otd.configuration.model.UserPrincipal;
import com.otd.otd_pointShop.application.point.model.*;
import com.otd.otd_pointShop.application.purchase.model.PurchaseHistoryRes;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
@RequestMapping("/api/OTD/pointshop")
public class PointshopController {
    private final PointshopService pointshopService;

    // 포인트 등록
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addPointItem(
            @RequestPart("data") PointPostReq dto,
            @RequestPart(value = "images", required = false) MultipartFile[] images,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = getLoginUserId(userPrincipal);
        try {
            pointshopService.createPointItem(dto, images, userId);
            log.info("[포인트 등록] 사용자 ID: {}, 제목: {}", userId, dto.getPointItemName());
            return ResponseEntity.ok(new PointApiResponse<>(true, "포인트 아이템 등록 완료"));
        } catch (Exception e) {
            log.error("포인트 등록 실패 - 사용자 ID: {}, 제목: {}", userId, dto.getPointItemName(), e);
            return ResponseEntity.internalServerError()
                    .body(new PointApiResponse<>(false, "서버 오류: " + e.getMessage()));
        }
    }

    // 포인트 전체 조회 (page)
    @GetMapping("/page")
    public ResponseEntity<?> getPointPageWithItemPage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Long userId = getLoginUserId(userPrincipal);
        Page<PointGetRes> resultPage = pointshopService.pointGetResList(userId, pageable);
        return ResponseEntity.ok(resultPage);
    }

    // 포인트 요약 리스트 조회
    @GetMapping("/list")
    public ResponseEntity<?> getPointList (
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Long userId = getLoginUserId(userPrincipal);
        List<PointListRes> list = pointshopService.getPointListByUser(userId, pageable);
        return ResponseEntity.ok(list);
    }

    // 키워드 기반 조회
    @GetMapping("/keyword")
    public ResultResponse<?> getPointKeywordByUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "") String keyword,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Long userId = getLoginUserId(userPrincipal);
        Set<String> result = pointshopService.getPointKeywordByUser(userId, keyword, pageable);
        return new ResultResponse<>(String.format("rows: %d", result.size()), result);
    }

    // 포인트 수정
    @PutMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePointItem(
            @RequestPart("data") PointPutReq dto,
            @RequestPart(value = "images", required = false) MultipartFile[] images,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = getLoginUserId(userPrincipal);
        try {
            pointshopService.updatePointItem(dto, images, userId);
            log.info("[포인트 수정] 사용자 ID: {}, 제목: {}", userId, dto.getPointItemName());
            return ResponseEntity.ok(new PointApiResponse<>(true, "포인트 수정 완료"));
        } catch (Exception e) {
            log.error("포인트 수정 실패 - 사용자 ID: {}, 제목: {}", userId, dto.getPointItemName(), e);
            return ResponseEntity.internalServerError()
                    .body(new PointApiResponse<>(false, "서버 오류: " + e.getMessage()));
        }
    }

    // 포인트 삭제
    @DeleteMapping("/point/delete")
    public ResponseEntity<?> deletePointItem(
            @RequestParam("pointId") Long pointId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = getLoginUserId(userPrincipal);
        try {
            pointshopService.deletePointItem(pointId, userId);
            log.info("[포인트 삭제] 사용자 ID: {}, pointId: {}", userId, pointId);
            return ResponseEntity.ok(new PointApiResponse<>(true, "포인트 삭제 완료"));
        } catch (Exception e) {
            log.error("포인트 삭제 실패 - 사용자 ID: {}, pointId: {}", userId, pointId, e);
            return ResponseEntity.internalServerError()
                    .body(new PointApiResponse<>(false, "서버 오류로 삭제 실패"));
        }
    }

    // 유저 포인트 조회
    @GetMapping("/user/points")
    public ResponseEntity<?> getUserPoints(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = getLoginUserId(userPrincipal);
        int balance = pointshopService.getUserPointBalance(userId);
        log.info("[포인트 조회] 사용자 ID: {}, 잔액: {}", userId, balance);
        return ResponseEntity.ok(new PointApiResponse<>(true, "포인트 잔액 조회 성공", balance));
    }

    // 구매 이력 조회
    @GetMapping("/purchase/history")
    public ResponseEntity<?> getUserPurchaseHistory(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = getLoginUserId(userPrincipal);
        List<PurchaseHistoryRes> history = pointshopService.getUserPurchaseHistory(userId);
        log.info("[구매 이력 조회] 사용자 ID: {}, 내역 개수: {}", userId, history.size());
        return ResponseEntity.ok(new PointApiResponse<>(true, "구매 이력 조회 성공", history));
    }

    // 로그인 유저 확인
    private Long getLoginUserId(UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        return userPrincipal.getSignedUserId();
    }
}
package com.otd.otd_pointshop.application.purchase;

import com.otd.configuration.model.UserPrincipal;
import com.otd.otd_pointshop.application.point.model.PointApiResponse;
import com.otd.otd_pointshop.application.purchase.model.PurchaseHistoryRes;
import com.otd.otd_pointshop.application.purchase.model.PurchasePostRes;
import com.otd.otd_pointshop.application.purchase.model.PurchaseUseRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/OTD/pointshop/purchase")
public class PurchaseHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;

    // [POST] 아이템 구매
    @PostMapping("/{pointId}")
    public ResponseEntity<PointApiResponse<PurchasePostRes>> purchaseItem(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long pointId
    ) {
        if (user == null) {
            log.warn("[구매 요청 거부] 로그인 필요 - pointId={}", pointId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(PointApiResponse.error("로그인이 필요합니다."));
        }

        Long userId = user.getSignedUserId();
        log.info("[구매 요청] userId={}, pointId={}", userId, pointId);

        try {
            PurchasePostRes result = purchaseHistoryService.purchaseItem(userId, pointId);
            return ResponseEntity.ok(PointApiResponse.success("구매 완료", result));

        } catch (EntityNotFoundException e) {
            log.warn("[구매 실패 - NotFound] userId={}, pointId={}, msg={}", userId, pointId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(PointApiResponse.error(e.getMessage()));

        } catch (IllegalArgumentException e) {
            log.warn("[구매 실패 - BadRequest] userId={}, pointId={}, msg={}", userId, pointId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(PointApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            log.error("[구매 실패 - ServerError] userId={}, pointId={}, error={}", userId, pointId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(PointApiResponse.error("서버 오류로 구매를 진행할 수 없습니다."));
        }
    }

    // [POST] 포인트 사용 (일반 차감용)
    @PostMapping("/use")
    public ResponseEntity<PointApiResponse<PurchasePostRes>> usePoint(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam int usedAmount,
            @RequestParam(required = false, defaultValue = "GENERAL_USE") String usageType
    ) {
        if (user == null) {
            log.warn("[포인트 사용 거부] 로그인 필요");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(PointApiResponse.error("로그인이 필요합니다."));
        }

        Long userId = user.getSignedUserId();
        log.info("[포인트 사용 요청] userId={}, usedAmount={}, usageType={}", userId, usedAmount, usageType);

        try {
            PurchasePostRes result = purchaseHistoryService.usePoint(userId, usedAmount, usageType);
            return ResponseEntity.ok(PointApiResponse.success("포인트 사용 완료", result));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(PointApiResponse.error(e.getMessage()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(PointApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            log.error("[포인트 사용 실패] userId={}, usedAmount={}, error={}", userId, usedAmount, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(PointApiResponse.error("서버 오류로 포인트 사용을 진행할 수 없습니다."));
        }
    }

    // [GET] 구매 이력 조회 (로그인 사용자)
    @GetMapping("/history/user")
    public ResponseEntity<PointApiResponse<List<PurchaseHistoryRes>>> getUserPurchaseHistory(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        if (user == null) {
            log.warn("[이력 조회 거부] 로그인 필요");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(PointApiResponse.error("로그인이 필요합니다."));
        }

        Long userId = user.getSignedUserId();
        log.info("[이력 조회 요청] userId={}", userId);

        List<PurchaseHistoryRes> history = purchaseHistoryService.getUserPurchaseHistory(userId);
        return ResponseEntity.ok(PointApiResponse.success("구매 이력 조회 성공", history));
    }

    // [GET] 구매 상세 조회 (단건)
    @GetMapping("/{purchaseId}")
    public ResponseEntity<PointApiResponse<PurchaseHistoryRes>> getPurchaseDetail(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long purchaseId
    ) {
        if (user == null) {
            log.warn("[상세 조회 거부] 로그인 필요");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(PointApiResponse.error("로그인이 필요합니다."));
        }

        Long userId = user.getSignedUserId();
        log.info("[구매 상세 조회 요청] userId={}, purchaseId={}", userId, purchaseId);

        try {
            PurchaseHistoryRes detail = purchaseHistoryService.getPurchaseDetail(purchaseId, userId);
            return ResponseEntity.ok(PointApiResponse.success("구매 상세 조회 성공", detail));

        } catch (EntityNotFoundException e) {
            log.warn("[상세 조회 실패 - NotFound] purchaseId={}, msg={}", purchaseId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(PointApiResponse.error("해당 쿠폰을 찾을 수 없습니다."));

        } catch (Exception e) {
            log.error("[상세 조회 실패 - ServerError] purchaseId={}, error={}", purchaseId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(PointApiResponse.error("서버 오류로 구매 상세 조회에 실패했습니다."));
        }
    }


    // [PATCH] 쿠폰 사용 처리
    @PatchMapping("/{purchaseId}/use")
    public ResponseEntity<PointApiResponse<PurchaseUseRes>> markAsUsed(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long purchaseId
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(PointApiResponse.error("로그인이 필요합니다."));
        }
        Long userId = user.getSignedUserId();

        try {
            PurchaseUseRes result = purchaseHistoryService.markAsUsed(purchaseId, userId);
            return ResponseEntity.ok(PointApiResponse.success("쿠폰 사용 처리 완료", result));

        } catch (EntityNotFoundException e) {
            log.warn("[쿠폰 사용 실패] purchaseId={}, msg={}", purchaseId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(PointApiResponse.error("해당 쿠폰을 찾을 수 없습니다."));

        } catch (IllegalArgumentException e) {
            log.warn("[쿠폰 이미 사용됨] purchaseId={}, msg={}", purchaseId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(PointApiResponse.error("이미 사용된 쿠폰입니다."));

        } catch (Exception e) {
            log.error("[쿠폰 사용 처리 중 서버 오류]", e);
            return ResponseEntity.internalServerError()
                    .body(PointApiResponse.error("서버 오류로 쿠폰 사용 처리에 실패했습니다."));
        }
    }

    // [GET] 구매 이력 조회 (특정 사용자)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PurchaseHistoryRes>> getUserPurchaseHistory(@PathVariable Long userId) {
        List<PurchaseHistoryRes> response = purchaseHistoryService.getUserPurchaseHistory(userId);
        return ResponseEntity.ok(response);
    }
}
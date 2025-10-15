package com.otd.otd_pointShop.application.purchase;

import com.otd.configuration.model.ResultResponse;
import com.otd.configuration.model.UserPrincipal;
import com.otd.otd_pointShop.application.point.model.PointApiResponse;
import com.otd.otd_pointShop.application.purchase.model.PurchasePointChargeReq;
import com.otd.otd_pointShop.application.purchase.model.PurchasePostReq;
import com.otd.otd_pointShop.application.purchase.model.PurchasePostRes;
import com.otd.otd_pointShop.application.stats.StatsService;
import com.otd.otd_user.application.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/OTD/pointshop/purchase")
@RequiredArgsConstructor
public class PurchaseHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;
    private final StatsService statsService;
    private final UserRepository userRepository;

    // 포인트 아이템 구매
    @PostMapping("/{pointId}")
    public ResponseEntity<?> purchaseItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long pointId
    ) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401)
                    .body(new PointApiResponse<>(false, "로그인이 필요합니다."));
        }

        PurchasePostRes res = purchaseHistoryService.purchaseItem(userPrincipal.getSignedUserId(), pointId);
        log.info("[구매 완료] userId={}, pointId={}, itemName={}",
                userPrincipal.getSignedUserId(), pointId, res.getPointItemName());

        return ResponseEntity.ok(new PointApiResponse<>(true, "구매 성공", res));
    }

    // 사용자 포인트 충전 (테스트)
    @PostMapping("/charge")
    public ResponseEntity<?> chargePoint(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody PurchasePointChargeReq req
    ) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401)
                    .body(new PointApiResponse<>(false, "로그인이 필요합니다."));
        }

        int amount = req.getAmount();
        if (amount <= 0) {
            return ResponseEntity.badRequest()
                    .body(new PointApiResponse<>(false, "1 이상의 금액을 충전해야 합니다."));
        }

        Long userId = userPrincipal.getSignedUserId();
        userRepository.updatePointByUserId(amount, userId);

        log.info("[포인트 충전] userId={}, amount={}", userId, amount);
        return ResponseEntity.ok(new PointApiResponse<>(true, "포인트 충전 완료"));
    }

    // (사용자) 본인 구매 이력 조회
    @GetMapping("/history/user")
    public ResponseEntity<ResultResponse<?>> getUserPurchaseHistory(HttpSession session) {
        Object loginUser = session.getAttribute("loginUser");

        if (loginUser == null) {
            return ResponseEntity.status(401)
                    .body(new ResultResponse<>("로그인 필요", List.of()));
        }

        Long userId = (Long) loginUser;
        var history = purchaseHistoryService.getUserPurchaseHistory(userId);

        return ResponseEntity.ok(
                new ResultResponse<>("구매 이력 조회 성공", history)
        );
    }

    // (관리자) 모든 사용자 구매 + 충전 이력 조회
    @GetMapping("/admin/history/all")
    public ResponseEntity<?> getAllHistories(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null || !userPrincipal.hasRole("ADMIN")) {
            return ResponseEntity.status(403)
                    .body(new PointApiResponse<>(false, "관리자 권한이 필요합니다."));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("purchases", purchaseHistoryService.getAllHistories());
        data.put("recharges", statsService.getMonthlySummary());

        return ResponseEntity.ok(new PointApiResponse<>(true, "전체 구매/충전 이력 조회 성공", data));
    }

    // (관리자) 월별/인기/상위 사용자 통계
    @GetMapping("/admin/stats")
    public ResponseEntity<?> getPurchaseStats(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null || !userPrincipal.hasRole("ADMIN")) {
            return ResponseEntity.status(403)
                    .body(new PointApiResponse<>(false, "관리자 권한이 필요합니다."));
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("monthly", statsService.getMonthlySummary());  // ✅ getMonthlyStats → getMonthlySummary
        stats.put("popularItems", statsService.getPopularItems());
        stats.put("topUsers", statsService.getTopUsers());

        return ResponseEntity.ok(new PointApiResponse<>(true, "포인트 통계 조회 성공", stats));
    }
}
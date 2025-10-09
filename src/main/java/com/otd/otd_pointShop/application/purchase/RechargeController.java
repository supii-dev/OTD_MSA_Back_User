package com.otd.otd_pointShop.application.purchase;

import com.otd.configuration.model.UserPrincipal;
import com.otd.otd_pointShop.application.point.model.PointApiResponse;
import com.otd.otd_pointShop.application.purchase.model.RechargeGetRes;
import com.otd.otd_pointShop.application.purchase.model.RechargePostReq;
import com.otd.otd_pointShop.application.purchase.model.RechargePostRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/OTD/pointshop/recharge")
@RequiredArgsConstructor
public class RechargeController {

    private final RechargeService rechargeService;

    // (관리자) 특정 유저 포인트 충전
    @PostMapping("/admin")
    public ResponseEntity<?> rechargePoint(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody RechargePostReq req
    ) {
        if (userPrincipal == null || !userPrincipal.hasRole("ADMIN")) {
            return ResponseEntity.status(403).body("관리자 권한이 필요합니다.");
        }

        RechargePostRes result = rechargeService.rechargePoint(req.getUserId(), req);
        return ResponseEntity.ok(new PointApiResponse<>(true, result));
    }

    // (관리자) 전체 유저 충전 내역 조회
    @GetMapping("/admin/history")
    public ResponseEntity<?> getAllRechargeHistories(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        if (userPrincipal == null || !userPrincipal.hasRole("ADMIN")) {
            return ResponseEntity.status(403).body("관리자 권한이 필요합니다.");
        }

        List<RechargeGetRes> histories = rechargeService.getRechargeHistories();
        return ResponseEntity.ok(new PointApiResponse<>(true, histories));
    }

    // (관리자) 총 포인트 통계
    @GetMapping("/admin/stats/total")
    public ResponseEntity<?> getTotalPointStats(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        if (userPrincipal == null || !userPrincipal.hasRole("ADMIN")) {
            return ResponseEntity.status(403).body("관리자 권한이 필요합니다.");
        }
        Integer total = rechargeService.getTotalRechargeAmount();
        return ResponseEntity.ok(new PointApiResponse<>(true, total));
    }

    // (관리자) 특정 유저 충전 이력 조회
    @GetMapping("/admin/history/user/{userId}")
    public ResponseEntity<?> getRechargeHistoryByUser(@PathVariable Long userId) {
        List<RechargePostRes> history = rechargeService.getRechargeHistoryByUserId(userId);
        return ResponseEntity.ok(new PointApiResponse<>(true, history));
    }

    // 내 포인트 잔액 확인
    @GetMapping("/balance/my")
    public ResponseEntity<?> getMyBalance(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(new PointApiResponse<>(false, "로그인이 필요합니다."));
        }

        Integer balance = rechargeService.getBalance(userPrincipal.getSignedUserId());
        return ResponseEntity.ok(new PointApiResponse<>(true, balance));
    }
}

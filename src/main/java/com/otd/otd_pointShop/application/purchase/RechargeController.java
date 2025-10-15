package com.otd.otd_pointShop.application.purchase;

import com.otd.configuration.model.UserPrincipal;
import com.otd.otd_pointShop.application.point.model.PointApiResponse;
import com.otd.otd_pointShop.application.stats.model.MonthlyRechargeStatsRes;
import com.otd.otd_pointShop.application.purchase.model.RechargeGetRes;
import com.otd.otd_pointShop.application.purchase.model.RechargePostReq;
import com.otd.otd_pointShop.application.purchase.model.RechargePostRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/OTD/pointshop/recharge")
@RequiredArgsConstructor
public class RechargeController {

    private final RechargeService rechargeService;

    // (관리자) 특정 유저 포인트 충전
    @PostMapping("/admin")
    public ResponseEntity<?> rechargeForUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody RechargePostReq req
    ) {
        if (userPrincipal == null || !userPrincipal.hasRole("ADMIN")) {
            return ResponseEntity.status(403)
                    .body(new PointApiResponse<>(false, "관리자 권한이 필요합니다."));
        }

        req.setAdminId(userPrincipal.getSignedUserId());
        RechargePostRes result = rechargeService.rechargePoint(req.getUserId(), req);
        Integer balance = rechargeService.getBalance(req.getUserId());

        log.info("[관리자 충전] adminId={}, userId={}, amount={}", req.getAdminId(), req.getUserId(), req.getAmount());
        return ResponseEntity.ok(new PointApiResponse<>(true, "포인트 충전 완료", result, balance));
    }

    // (관리자) 전체 유저 충전 내역 조회
    @GetMapping("/admin/history")
    public ResponseEntity<?> getAllRechargeHistories(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null || !userPrincipal.hasRole("ADMIN")) {
            return ResponseEntity.status(403)
                    .body(new PointApiResponse<>(false, "관리자 권한이 필요합니다."));
        }

        List<RechargeGetRes> histories = rechargeService.getRechargeHistories();
        return ResponseEntity.ok(new PointApiResponse<>(true, "전체 충전 내역 조회 성공", histories, null));
    }

    // (관리자) 특정 유저 충전 이력 조회
    @GetMapping("/admin/history/user/{userId}")
    public ResponseEntity<?> getRechargeHistoryByUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long userId
    ) {
        if (userPrincipal == null || !userPrincipal.hasRole("ADMIN")) {
            return ResponseEntity.status(403)
                    .body(new PointApiResponse<>(false, "관리자 권한이 필요합니다."));
        }

        List<RechargePostRes> history = rechargeService.getRechargeHistoryByUserId(userId);
        Integer balance = rechargeService.getBalance(userId);
        return ResponseEntity.ok(new PointApiResponse<>(true, "특정 유저 충전 이력 조회 성공", history, balance));
    }

    // (관리자) 총 충전 포인트 합계 조회
    @GetMapping("/admin/stats/total")
    public ResponseEntity<?> getTotalPointStats(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null || !userPrincipal.hasRole("ADMIN")) {
            return ResponseEntity.status(403)
                    .body(new PointApiResponse<>(false, "관리자 권한이 필요합니다."));
        }

        Integer total = rechargeService.getTotalRechargeAmount();
        return ResponseEntity.ok(new PointApiResponse<>(true, "총 충전 포인트 합계 조회 성공", total, null));
    }

    // (관리자) 월별 충전 내역 통계
    @GetMapping("/admin/stats/monthly")
    public ResponseEntity<?> getMonthlyRechargeStats(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null || !userPrincipal.hasRole("ADMIN")) {
            return ResponseEntity.status(403)
                    .body(new PointApiResponse<>(false, "관리자 권한이 필요합니다."));
        }

        Long adminId = userPrincipal.getSignedUserId();
        List<MonthlyRechargeStatsRes> stats = rechargeService.getMonthlyRechargeStatsByAdmin(adminId);

        return ResponseEntity.ok(new PointApiResponse<>(true, "월별 충전 통계 조회 성공", stats, null));
    }

    // (사용자) 내 충전 이력 조회
    @GetMapping("/history/my")
    public ResponseEntity<?> getMyRechargeHistory(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401)
                    .body(new PointApiResponse<>(false, "로그인이 필요합니다."));
        }

        Long userId = userPrincipal.getSignedUserId();
        List<RechargePostRes> history = rechargeService.getRechargeHistoryByUserId(userId);
        Integer balance = rechargeService.getBalance(userId);

        return ResponseEntity.ok(new PointApiResponse<>(true, "본인 충전 이력 조회 성공", history, balance));
    }

    // (사용자) 내 포인트 잔액 조회
    @GetMapping("/balance")
    public ResponseEntity<?> getMyBalance(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401)
                    .body(new PointApiResponse<>(false, "로그인이 필요합니다."));
        }

        Long userId = userPrincipal.getSignedUserId();
        Integer balance = rechargeService.getBalance(userId);
        return ResponseEntity.ok(new PointApiResponse<>(true, "내 포인트 잔액 조회 성공", balance, balance));
    }
}
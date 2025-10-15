package com.otd.otd_pointShop.application.stats;

import com.otd.configuration.model.UserPrincipal;
import com.otd.otd_pointShop.application.point.model.PointApiResponse;
import com.otd.otd_pointShop.application.stats.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/OTD/pointshop/admin/stats")
public class StatsController {

    private final StatsService statsService;

    // 전체 유저 월별 요약 통계
    @GetMapping("/summary")
    public ResponseEntity<?> getMonthlySummary(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (!isAdmin(userPrincipal)) {
            return forbidden("관리자 권한이 필요합니다.");
        }

        List<MonthlySummaryStatsRes> stats = statsService.getMonthlySummary();
        return ResponseEntity.ok(new PointApiResponse<>(true, "전체 월별 충전/구매 통계 조회 성공", stats, null));
    }

    // 특정 유저 요약
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getMonthlySummaryByUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long userId
    ) {
        if (!isAdmin(userPrincipal)) {
            return forbidden("관리자 권한이 필요합니다.");
        }

        List<MonthlySummaryStatsRes> stats = statsService.getMonthlySummaryByUser(userId);
        return ResponseEntity.ok(new PointApiResponse<>(true, "특정 유저 월별 통계 조회 성공", stats, null));
    }

    // 월별 상세 내역 조회 (충전 + 구매)
    @GetMapping("/detail")
    public ResponseEntity<?> getMonthlyDetail(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String month,
            @RequestParam(required = false) Long userId
    ) {
        if (!isAdmin(userPrincipal)) {
            return forbidden("관리자 권한이 필요합니다.");
        }

        List<MonthlyDetailRes> details = statsService.getMonthlyDetail(month, userId);
        return ResponseEntity.ok(new PointApiResponse<>(true, "월별 상세 내역 조회 성공", details, null));
    }

    // TOP 10 유저 통계 (충전/구매)
    @GetMapping("/top-users")
    public ResponseEntity<?> getTopUsers(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (!isAdmin(userPrincipal)) {
            return forbidden("관리자 권한이 필요합니다.");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("topRechargeUsers", statsService.getTopRechargeUsers());
        data.put("topPurchaseUsers", statsService.getTopPurchaseUsers());

        return ResponseEntity.ok(new PointApiResponse<>(true, "Top10 유저 통계 조회 성공", data, null));
    }

    // 인기 아이템
    @GetMapping("/popular-items")
    public ResponseEntity<?> getPopularItems(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (!isAdmin(userPrincipal)) {
            return forbidden("관리자 권한이 필요합니다.");
        }

        List<PopularItemRes> items = statsService.getPopularItems();
        return ResponseEntity.ok(new PointApiResponse<>(true, "인기 포인트 아이템 조회 성공", items, null));
    }

    // 공통 관리자 권한 체크
    private boolean isAdmin(UserPrincipal userPrincipal) {
        return userPrincipal != null && userPrincipal.hasRole("ADMIN");
    }

    private ResponseEntity<PointApiResponse<?>> forbidden(String message) {
        return ResponseEntity.status(403).body(new PointApiResponse<>(false, message));
    }
}
package com.otd.otd_pointShop.application.purchase;

import com.otd.configuration.model.UserPrincipal;
import com.otd.otd_pointShop.application.point.model.PointApiResponse;
import com.otd.otd_pointShop.application.purchase.model.RechargePostReq;
import com.otd.otd_pointShop.application.purchase.model.RechargePostRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pointshop/recharge")
@RequiredArgsConstructor
public class RechargeController {

    private final RechargeService rechargeService;

    // 관리자 전용 페이지, 특정 유저 포인트 충전
    @PostMapping
    public ResponseEntity<?> rechargePoint(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody RechargePostReq req
    ) {
        if (userPrincipal == null || !userPrincipal.hasRole("ADMIN")) {
            return ResponseEntity.status(403).body("관리자 권한이 필요합니다.");
        }

        RechargePostRes result = rechargeService.rechargePoint(req);
        return ResponseEntity.ok(new PointApiResponse<>(true, result));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyBalance(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Integer balance = rechargeService.getBalance(userPrincipal.getSignedUserId());
        return ResponseEntity.ok(new PointApiResponse<>(true, balance));
    }

    @GetMapping("/history/user/{userId}")
    public ResponseEntity<?> getHistoryUser(@PathVariable Long userId) {
        List<RechargePostRes> history = rechargeService.getRechargeHistoryByUser(userId);
        return ResponseEntity.ok(new PointApiResponse<>(true, history));
    }
}

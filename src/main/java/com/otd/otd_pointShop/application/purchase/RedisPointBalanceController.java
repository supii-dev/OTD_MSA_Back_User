package com.otd.otd_pointShop.application.purchase;

import com.otd.configuration.model.UserPrincipal;
import com.otd.otd_pointShop.application.point.model.PointApiResponse;
import com.otd.otd_pointShop.application.purchase.model.PurchaseBalanceRechargeReq;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pointshop/balance")
@RequiredArgsConstructor
public class RedisPointBalanceController {

    private final PointBalanceService pointBalanceService;

    @PostMapping("/recharge")
    public ResponseEntity<?> rechargeBalance(
            @RequestBody PurchaseBalanceRechargeReq rechargeReq,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        if(userPrincipal == null || !userPrincipal.hasRole("ADMIN")) {
            return ResponseEntity.status(403).body("관리자 권한이 필요합니다.");
        }
        pointBalanceService.pointIncrement(rechargeReq.getUserId(), rechargeReq.getAmount());
        return ResponseEntity.ok(new PointApiResponse<>(true, "포인트 충전 완료"));
    }

    @GetMapping
    public ResponseEntity<?> getPointBalance(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다");
        }
        Long userId = userPrincipal.getSignedUserId();
        Integer pointBalance = pointBalanceService.getPointBalance(userId);
        return ResponseEntity.ok(new PointApiResponse<>(true, pointBalance));
    }
}

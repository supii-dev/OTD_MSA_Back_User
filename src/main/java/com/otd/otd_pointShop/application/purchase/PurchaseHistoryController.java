package com.otd.otd_pointShop.application.purchase;

import com.otd.configuration.model.UserPrincipal;
import com.otd.otd_pointShop.application.point.model.PointApiResponse;
import com.otd.otd_pointShop.application.purchase.model.PurchasePointChargeReq;
import com.otd.otd_pointShop.application.purchase.model.PurchasePostReq;
import com.otd.otd_pointShop.application.purchase.model.PurchasePostRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pointshop/purchase")
@RequiredArgsConstructor
public class PurchaseHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;
    private final PointBalanceService pointBalanceService;

    @PostMapping
    public ResponseEntity<?> purchase(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody PurchasePostReq req
    ) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(new PointApiResponse<>(false, "로그인이 필요합니다."));
        }
        PurchasePostRes res = purchaseHistoryService.purchaseItem(userPrincipal.getSignedUserId(), req.getPointId());
        return ResponseEntity.ok(new PointApiResponse<>(true, res));
    }

    @PostMapping("/charge")
    public ResponseEntity<?> chargePoint(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody PurchasePointChargeReq req
    ) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다");
        }

        Long userId = userPrincipal.getSignedUserId();
        pointBalanceService.pointIncrement(userId, req.getAmount());
        return ResponseEntity.ok(new PointApiResponse<>(true, "포인트가 충전되었습니다."));
    }

    @GetMapping
    public ResponseEntity<?> getPurchases(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if(userPrincipal ==null) {
            return ResponseEntity.status(401).body(new PointApiResponse<>(false, "로그인이 필요합니다."));
    }

    List<PurchasePostRes> purchases = purchaseHistoryService.getUserPurchases(userPrincipal.getSignedUserId());
        return ResponseEntity.ok(new PointApiResponse<>(true, purchases));
    }
}
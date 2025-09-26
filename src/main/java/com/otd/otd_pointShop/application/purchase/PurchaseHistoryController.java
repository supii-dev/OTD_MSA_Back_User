package com.otd.otd_pointShop.application.purchase;

import com.otd.otd_pointShop.application.point.model.PointApiResponse;
import com.otd.otd_pointShop.application.purchase.model.PurchaseHistoryPostReq;
import com.otd.configuration.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pointshop/purchase")
@RequiredArgsConstructor
public class PurchaseHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;

    @PostMapping
    public ResponseEntity<?> purchase(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody PurchaseHistoryPostReq dto
    ) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        purchaseHistoryService.createPurchase(userPrincipal.getSignedUserId(), dto);
        return ResponseEntity.ok(new PointApiResponse<>(true, "구매 완료"));
    }
}
package com.otd.otd_pointShop.application.purchase;

import com.otd.configuration.model.UserPrincipal;
import com.otd.otd_pointShop.application.point.model.PointApiResponse;
import com.otd.otd_pointShop.application.purchase.model.PurchasePointChargeReq;
import com.otd.otd_pointShop.application.purchase.model.PurchasePostReq;
import com.otd.otd_pointShop.application.purchase.model.PurchasePostRes;
import com.otd.otd_user.application.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/OTD/pointshop/purchase")
@RequiredArgsConstructor
public class PurchaseHistoryController {

    private final PurchaseHistoryService purchaseHistoryService;
    private final UserRepository userRepository;

    @PostMapping("/{pointId}")
    public ResponseEntity<?> purchase(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long pointId // ⚠ 수정 필요
    ) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(new PointApiResponse<>(false, "로그인이 필요합니다."));
        }
        PurchasePostRes res = purchaseHistoryService.purchaseItem(userPrincipal.getSignedUserId(), pointId); // ⚠ 수정됨
        return ResponseEntity.ok(new PointApiResponse<>(true, "구매 성공", res));
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
        int amount = req.getAmount();
        if (amount <= 0) {
            return ResponseEntity.badRequest().body("1 이상의 금액을 충전해야 합니다.");
        }

        userRepository.updatePointByUserId(amount, userId);

        return ResponseEntity.ok(new PointApiResponse<>(true, "포인트가 충전되었습니다."));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getPurchases(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if(userPrincipal == null) {
            return ResponseEntity.status(401).body(new PointApiResponse<>(false, "로그인이 필요합니다."));
        }

        List<PurchasePostRes> purchases = purchaseHistoryService.getUserPurchases(userPrincipal.getSignedUserId());
        return ResponseEntity.ok(new PointApiResponse<>(true, "구매 이력 조회 성공", purchases));
    }
}
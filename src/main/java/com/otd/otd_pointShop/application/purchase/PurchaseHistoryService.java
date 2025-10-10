package com.otd.otd_pointShop.application.purchase;

import com.otd.otd_pointShop.application.purchase.model.PurchasePostRes;
import com.otd.otd_pointShop.entity.Point;
import com.otd.otd_pointShop.entity.PurchaseHistory;
import com.otd.otd_user.entity.User;
import com.otd.otd_pointShop.repository.PointRepository;
import com.otd.otd_pointShop.repository.PurchaseHistoryRepository;
import com.otd.otd_user.application.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseHistoryService {
    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final PointRepository pointRepository;
    private final UserRepository userRepository;
    private final PointBalanceService pointBalanceService;

    // point 상품 구매
    @Transactional
    public PurchasePostRes purchaseItem(Long userId, Long pointId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Point point = pointRepository.findById(pointId)
                .orElseThrow(() -> new EntityNotFoundException("포인트 아이템을 찾을 수 없습니다."));
        int pointBalance = pointBalanceService.getPointBalance(userId);
        int pointPrice = point.getPointScore();

        if (pointBalance < pointPrice) {
            throw new IllegalArgumentException("포인트가 부족합니다. 현재 잔액: " + pointBalance + " / 필요 포인트: " + pointPrice);
        }

        pointBalanceService.pointDecrement(userId, pointPrice);

        PurchaseHistory history = new PurchaseHistory();
        history.setUser(user);
        history.setPoint(point);
        purchaseHistoryRepository.save(history);

        String imageUrl = point.getPointItemImage() != null && !point.getPointItemImage().isEmpty()
                ? point.getPointItemImage().get(0).getImageUrl()
                : null;

        return PurchasePostRes.builder()
                .purchaseId(history.getPurchaseId())
                .pointId(point.getPointId())
                .pointItemName(point.getPointItemName())
                .pointScore(pointPrice)
                .pointItemImage(imageUrl)
                .purchaseTime(history.getPurchaseTime())
                .build();
    }

    // 전체 구매 이력 조회
    @Transactional
    public List<PurchasePostRes> getUserPurchases(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        return purchaseHistoryRepository.findByUser(user).stream().map(purchase -> {
            Point point = purchase.getPoint();
            String imageUrl = point.getPointItemImage() != null && !point.getPointItemImage().isEmpty()
                    ? point.getPointItemImage().get(0).getImageUrl()
                    : null;

            return PurchasePostRes.builder()
                    .purchaseId(purchase.getPurchaseId())
                    .pointId(point.getPointId())
                    .pointItemName(point.getPointItemName())
                    .pointScore(point.getPointScore())
                    .pointItemImage(imageUrl)
                    .purchaseTime(purchase.getPurchaseTime())
                    .build();
        }).collect(Collectors.toList());
    }
}
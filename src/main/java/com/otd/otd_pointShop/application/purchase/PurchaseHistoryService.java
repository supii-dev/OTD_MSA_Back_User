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

    @Transactional
    public PurchasePostRes purchaseItem(Long userId, Long pointId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Point point = pointRepository.findById(pointId)
                .orElseThrow(() -> new EntityNotFoundException("포인트 아이템을 찾을 수 없습니다."));

        PurchaseHistory history = new PurchaseHistory();
        history.setUser(user);
        history.setPointItem(point);
        purchaseHistoryRepository.save(history);

        return PurchasePostRes.builder()
                .purchaseId(history.getPurchaseId())
                .pointId(point.getPointId())
                .pointItemName(point.getPointItemName())
                .pointScore(point.getPointScore())
                .pointItemImage(point.getPointItemImage().isEmpty() ? null : point.getPointItemImage().get(0).getImageUrl())
                .purchaseTime(history.getPurchaseTime())
                .build();
    }
    public List<PurchasePostRes> getUserPurchases(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        return purchaseHistoryRepository.findByUser_UserId(user).stream().map(purchase -> {
            Point point = purchase.getPointItem();
                    return PurchasePostRes.builder()
                            .purchaseId(purchase.getPurchaseId())
                            .pointId(point.getPointId())
                            .pointItemName(point.getPointItemName())
                            .pointScore(point.getPointScore())
                            .pointItemImage(point.getPointItemImage().isEmpty() ? null : point.getPointItemImage().get(0).getImageUrl())
                            .purchaseTime(purchase.getPurchaseTime())
                            .build();
                }).collect(Collectors.toList());
    }
}
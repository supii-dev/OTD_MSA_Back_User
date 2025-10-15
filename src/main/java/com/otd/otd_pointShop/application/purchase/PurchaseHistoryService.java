package com.otd.otd_pointShop.application.purchase;

import com.otd.otd_pointShop.application.purchase.model.PurchaseHistoryRes;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.otd.otd_pointShop.application.purchase.PurchaseHistoryService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseHistoryService {
    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    // 포인트 상품 구매
    @Transactional
    public PurchasePostRes purchaseItem(Long userId, Long pointId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Point point = pointRepository.findById(pointId)
                .orElseThrow(() -> new EntityNotFoundException("포인트 아이템을 찾을 수 없습니다."));

        int balance = user.getPoint();
        int price = point.getPointScore();

        if (balance < price) {
            throw new IllegalArgumentException("포인트가 부족합니다. (잔액: " + balance + ", 필요: " + price + ")");
        }

        user.setPoint(balance - price);
        userRepository.save(user);

        PurchaseHistory history = new PurchaseHistory();
        history.setUser(user);
        history.setPoint(point);
        purchaseHistoryRepository.save(history);

        String imageUrl = (point.getPointItemImage() != null && !point.getPointItemImage().isEmpty())
                ? point.getPointItemImage().get(0).getImageUrl()
                : null;

        return PurchasePostRes.builder()
                .purchaseId(history.getPurchaseId())
                .pointId(point.getPointId())
                .pointItemName(point.getPointItemName())
                .pointScore(price)
                .pointItemImage(imageUrl)
                .purchaseAt(history.getPurchaseAt())
                .build();
    }

    // (사용자) 구매 이력 조회
    public List<PurchasePostRes> getUserPurchases(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return purchaseHistoryRepository.findByUser(user).stream()
                .map(purchase -> {
                    Point point = purchase.getPoint();
                    String imageUrl = (point.getPointItemImage() != null && !point.getPointItemImage().isEmpty())
                            ? point.getPointItemImage().get(0).getImageUrl()
                            : null;

                    return PurchasePostRes.builder()
                            .purchaseId(purchase.getPurchaseId())
                            .pointId(point.getPointId())
                            .pointItemName(point.getPointItemName())
                            .pointScore(point.getPointScore())
                            .pointItemImage(imageUrl)
                            .purchaseAt(purchase.getPurchaseAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // (관리자) 전체 구매 이력 조회
    public List<PurchaseHistoryRes> getAllHistories() {
        return purchaseHistoryRepository.findAll().stream().map(purchase -> {
            Point point = purchase.getPoint();
            String imageUrl = (point.getPointItemImage() != null && !point.getPointItemImage().isEmpty())
                    ? point.getPointItemImage().get(0).getImageUrl()
                    : null;

            return PurchaseHistoryRes.builder()
                    .purchaseId(purchase.getPurchaseId())
                    .pointId(point.getPointId())
                    .pointItemName(point.getPointItemName())
                    .pointScore(point.getPointScore())
                    .pointItemImage(imageUrl)
                    .purchaseAt(purchase.getPurchaseAt())
                    .build();
        }).collect(Collectors.toList());
    }

    // memberId 기반
    public List<PurchaseHistoryRes> getHistory(Integer memberId) {
        return purchaseHistoryRepository.findByUser_UserId(memberId.longValue())
                .stream()
                .map(PurchaseHistoryRes::fromEntity)
                .toList();
    }

    // controller method
    public List<PurchaseHistoryRes> getPurchaseHistoryByUser(Long userId) {
        return purchaseHistoryRepository.findByUser_UserId(userId)
                .stream()
                .map(PurchaseHistoryRes::fromEntity)
                .toList();
    }
}
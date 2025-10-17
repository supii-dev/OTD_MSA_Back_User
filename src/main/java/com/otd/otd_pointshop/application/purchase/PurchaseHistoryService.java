package com.otd.otd_pointshop.application.purchase;

import com.otd.otd_pointshop.application.purchase.model.PurchaseHistoryRes;
import com.otd.otd_pointshop.application.purchase.model.PurchasePostRes;
import com.otd.otd_pointshop.entity.Point;
import com.otd.otd_pointshop.entity.PurchaseHistory;
import com.otd.otd_user.entity.User;
import com.otd.otd_pointshop.repository.PointRepository;
import com.otd.otd_pointshop.repository.PurchaseHistoryRepository;
import com.otd.otd_user.application.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseHistoryService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    // [POST] 포인트 상품 구매
    @Transactional
    public PurchasePostRes purchaseItem(Long userId, Long pointId) {
        // 사용자 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 포인트 아이템 검증
        Point point = pointRepository.findById(pointId)
                .orElseThrow(() -> new EntityNotFoundException("요청한 포인트 아이템이 존재하지 않습니다."));

        int balance = user.getPoint();
        int price = point.getPointScore();

        // 포인트 잔액 확인
        if (balance < price) {
            throw new IllegalArgumentException(
                    String.format("포인트가 부족합니다. (잔액: %dP, 필요: %dP)", balance, price)
            );
        }

        // 포인트 차감
        user.setPoint(balance - price);
        userRepository.save(user);

        // 구매 이력 저장
        PurchaseHistory history = new PurchaseHistory();
        history.setUser(user);
        history.setPoint(point);
        purchaseHistoryRepository.save(history);

        log.info("[구매 완료] userId={}, pointId={}, 잔액={}", userId, pointId, user.getPoint());

        return PurchasePostRes.fromEntity(history);
    }

    // [POST] 포인트 사용
    @Transactional
    public PurchasePostRes usePoint(Long userId, int usedAmount, String usageType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        int balance = user.getPoint();
        if (balance < usedAmount) {
            throw new IllegalArgumentException(
                    String.format("포인트가 부족합니다. (잔액: %dP, 필요: %dP)", balance, usedAmount)
            );
        }

        user.setPoint(balance - usedAmount);
        userRepository.save(user);

        PurchaseHistory history = new PurchaseHistory();
        history.setUser(user);
        history.setUsageType(usageType != null ? usageType : "GENERAL_USE");
        purchaseHistoryRepository.save(history);

        log.info("[포인트 사용 완료] userId={}, used={}, usageType={}, remain={}",
                userId, usedAmount, usageType, user.getPoint());

        return PurchasePostRes.builder()
                .purchaseId(history.getPurchaseId())
                .pointId(null)
                .pointItemName("포인트 사용 (" + (usageType != null ? usageType : "기타") + ")")
                .pointScore(usedAmount)
                .pointItemImage(null)
                .purchaseAt(history.getPurchaseAt())
                .build();
    }

    // [GET] (관리자) 전체 구매 이력 조회
    public List<PurchaseHistoryRes> getAllHistories() {
        List<PurchaseHistory> historyList = purchaseHistoryRepository.findAll();
        return historyList.stream()
                .map(PurchaseHistoryRes::fromEntity)
                .toList();
    }

    // [GET] (사용자) 개인 구매 이력 조회
    public List<PurchaseHistoryRes> getUserPurchaseHistory(Long userId) {
        List<PurchaseHistory> historyList = purchaseHistoryRepository.findByUser_UserId(userId);
        return historyList.stream()
                .map(PurchaseHistoryRes::fromEntity)
                .toList();
    }

    // [GET] memberId 기반 중복 제거
    public List<PurchaseHistoryRes> getHistory(Long userId) {
        return getUserPurchaseHistory(userId);
    }

    // [GET] controller method
    public List<PurchaseHistoryRes> getPurchaseHistoryByUser(Long userId) {
        return getUserPurchaseHistory(userId);
    }
}
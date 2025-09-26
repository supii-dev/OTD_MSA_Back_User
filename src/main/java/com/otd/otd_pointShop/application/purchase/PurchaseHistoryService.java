package com.otd.otd_pointShop.application.purchase;

import com.otd.otd_pointShop.application.purchase.model.PurchaseHistoryPostReq;
import com.otd.otd_pointShop.entity.Point;
import com.otd.otd_pointShop.entity.PurchaseHistory;
import com.otd.otd_user.entity.User;
import com.otd.otd_pointShop.repository.PointRepository;
import com.otd.otd_pointShop.repository.PurchaseHistoryRepository;
import com.otd.otd_user.application.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseHistoryService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createPurchase(Long userId, PurchaseHistoryPostReq dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Point pointItem = pointRepository.findById(dto.getPointId())
                .orElseThrow(() -> new RuntimeException("포인트 아이템을 찾을 수 없습니다."));

        PurchaseHistory history = new PurchaseHistory();
        history.setUser(user);
        history.setPointItem(pointItem);

        purchaseHistoryRepository.save(history);
    }
}
package com.otd.otd_pointShop.application.purchase;

import com.otd.otd_pointShop.application.purchase.model.RechargeGetRes;
import com.otd.otd_pointShop.application.purchase.model.RechargePostReq;
import com.otd.otd_pointShop.application.purchase.model.RechargePostRes;
import com.otd.otd_pointShop.entity.RechargeHistory;
import com.otd.otd_pointShop.repository.RechargeHistoryRepository;
import com.otd.otd_user.application.user.UserRepository;
import com.otd.otd_user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RechargeService {

    private final PointBalanceService pointBalanceService;
    private final RechargeHistoryRepository rechargeHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public RechargePostRes rechargePoint(Long userId, RechargePostReq req) {
        if (req.getAmount() == null || req.getAmount() <= 0) {
            throw new IllegalArgumentException("충전 포인트는 1 이상이어야 합니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

        // 포인트 충전
        pointBalanceService.pointIncrement(userId, req.getAmount());

        // 충전 이력 저장
        RechargeHistory rechargeHistory = RechargeHistory.builder()
                .user(user)
                .amount(req.getAmount())
                .build();

        rechargeHistoryRepository.save(rechargeHistory);

        return RechargePostRes.builder()
                .rechargeId(rechargeHistory.getRechargeId())
                .amount(rechargeHistory.getAmount())
                .rechargeTime(rechargeHistory.getRechargeTime())
                .build();
    }

    public List<RechargeGetRes> getRechargeHistories() {
        return rechargeHistoryRepository.findAll().stream()
                .map(h -> RechargeGetRes.builder()
                        .rechargeId(h.getRechargeId())
                        .amount(h.getAmount())
                        .rechargeTime(h.getRechargeTime())
                        .build())
                .collect(Collectors.toList());
    }

    public List<RechargePostRes> getRechargeHistoryByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));
        return rechargeHistoryRepository.findByUser_UserId(userId).stream().map(history ->
                RechargePostRes.builder()
                        .rechargeId(history.getRechargeId())
                        .amount(history.getAmount())
                        .rechargeTime(history.getRechargeTime())
                        .build()
        ).toList();
    }
}

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

    private final RechargeHistoryRepository rechargeHistoryRepository;
    private final UserRepository userRepository;

    // (관리자) 포인트 충전
    @Transactional
    public RechargePostRes rechargePoint(Long userId, RechargePostReq req) {
        Integer amount = req.getAmount();
        if ( amount== null || amount <= 0) {
            throw new IllegalArgumentException("충전 포인트는 1 이상이어야 합니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));


        // 충전 이력 저장
        RechargeHistory rechargeHistory = RechargeHistory.builder()
                .user(user)
                .amount(amount)
                .build();

        rechargeHistoryRepository.save(rechargeHistory);

        return RechargePostRes.builder()
                .rechargeId(rechargeHistory.getRechargeId())
                .amount(rechargeHistory.getAmount())
                .rechargeTime(rechargeHistory.getRechargeTime())
                .build();
    }

    // (관리자) 전체 충전 이력 조회
    public List<RechargeGetRes> getRechargeHistories() {
        return rechargeHistoryRepository.findAll().stream()
                .map(history -> RechargeGetRes.builder()
                        .rechargeId(history.getRechargeId())
                        .amount(history.getAmount())
                        .rechargeTime(history.getRechargeTime())
                        .build())
                .toList();
    }

    // (관리자) 총 포인트 통계
    public Integer getTotalRechargeAmount() {
        return rechargeHistoryRepository.findAll().stream()
                .mapToInt(RechargeHistory::getAmount)
                .sum();
    }

    // (관리자) 특정 유저 충전 이력 조회
    public List<RechargePostRes> getRechargeHistoryByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

        return rechargeHistoryRepository.findByUser_UserId(userId).stream().map(history ->
                RechargePostRes.builder()
                        .rechargeId(history.getRechargeId())
                        .amount(history.getAmount())
                        .rechargeTime(history.getRechargeTime())
                        .build())
                .collect(Collectors.toList());
    }

    // 포인트 잔액 조회
    public Integer getBalance(Long userId) {
        return rechargeHistoryRepository.findByUser_UserId(userId).stream()
                .mapToInt(RechargeHistory::getAmount)
                .sum();
    }
}

package com.otd.otd_pointShop.application.purchase;

import com.otd.otd_admin.application.admin.Repository.AdminUserRepository;
import com.otd.otd_pointShop.application.stats.model.MonthlyRechargeStatsRes;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RechargeService {

    private final RechargeHistoryRepository rechargeHistoryRepository;
    private final UserRepository userRepository;
    private final AdminUserRepository adminUserRepository;

    // (관리자) 특정 유저 포인트 충전
    @Transactional
    public RechargePostRes rechargePoint(Long userId, RechargePostReq req) {
        if (req.getAdminId() == null || !adminUserRepository.existsById(req.getAdminId().intValue())) {
            throw new EntityNotFoundException("유효하지 않은 관리자 ID입니다.");
        }

        if (req.getAmount() == null || req.getAmount() <= 0) {
            throw new IllegalArgumentException("충전 포인트는 1 이상이어야 합니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        int updatedPoint = user.getPoint() + req.getAmount();
        user.setPoint(updatedPoint);
        userRepository.save(user);

        RechargeHistory history = RechargeHistory.builder()
                .adminId(req.getAdminId())
                .user(user)
                .amount(req.getAmount())
                .rechargeAt(LocalDateTime.now())
                .build();
        rechargeHistoryRepository.save(history);

        log.info("[관리자 포인트 충전] adminId={}, userId={}, name={}, amount={}, balance={}",
                req.getAdminId(), userId, user.getName(), req.getAmount(), updatedPoint);

        return RechargePostRes.builder()
                .rechargeId(history.getRechargeId())
                .adminId(req.getAdminId())
                .userId(user.getUserId())
                .name(user.getName())
                .amount(req.getAmount())
                .balance(updatedPoint)
                .rechargeAt(history.getRechargeAt())
                .build();
    }

    // (관리자) 전체 충전 이력 조회
    public List<RechargeGetRes> getRechargeHistories() {
        return rechargeHistoryRepository.findAll().stream()
                .map(h -> RechargeGetRes.builder()
                        .rechargeId(h.getRechargeId())
                        .adminId(h.getAdminId())
                        .userId(h.getUser().getUserId())
                        .name(h.getUser().getName())
                        .amount(h.getAmount())
                        .rechargeAt(h.getRechargeAt())
                        .build())
                .collect(Collectors.toList());
    }

    // (관리자/사용자 공용) 특정 유저 충전 이력 조회
    public List<RechargePostRes> getRechargeHistoryByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return rechargeHistoryRepository.findByUser_UserId(userId).stream()
                .map(h -> RechargePostRes.builder()
                        .rechargeId(h.getRechargeId())
                        .adminId(h.getAdminId())
                        .userId(user.getUserId())
                        .name(user.getName())
                        .amount(h.getAmount())
                        .balance(user.getPoint())
                        .rechargeAt(h.getRechargeAt())
                        .build())
                .collect(Collectors.toList());
    }

    // (관리자/사용자 공용) 포인트 잔액 조회
    public Integer getBalance(Long userId) {
        return userRepository.findById(userId)
                .map(User::getPoint)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
    }

    // 사용자 전용
    public Integer getBalanceByUserId(Long userId) {
        return getBalance(userId);
    }

    // 관리자 월별 충전 통계
    public List<MonthlyRechargeStatsRes> getMonthlyRechargeStatsByAdmin(Long adminId) {
        List<Object[]> rawData = rechargeHistoryRepository.findMonthlyRechargeStatsByAdmin(adminId);
        return rawData.stream()
                .map(obj -> new MonthlyRechargeStatsRes(
                        (String) obj[0],
                        ((Number) obj[1]).longValue()
                ))
                .toList();
    }

    // (관리자) 총 충전 포인트 합계
    public Integer getTotalRechargeAmount() {
        return rechargeHistoryRepository.findAll().stream()
                .mapToInt(RechargeHistory::getAmount)
                .sum();
    }
}
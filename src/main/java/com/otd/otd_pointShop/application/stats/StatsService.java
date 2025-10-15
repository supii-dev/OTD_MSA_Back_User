package com.otd.otd_pointShop.application.stats;

import com.otd.otd_pointShop.application.stats.model.*;

import com.otd.otd_pointShop.repository.StatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository statsRepository;

    // 전체 요약
    public List<MonthlySummaryStatsRes> getMonthlySummary() {
        return mergeStats(
                statsRepository.findMonthlyRechargeTotals(),
                statsRepository.findMonthlyPurchaseTotals()
        );
    }

    // 유저별 요약
    public List<MonthlySummaryStatsRes> getMonthlySummaryByUser(Long userId) {
        return mergeStats(
                statsRepository.findMonthlyRechargeTotalsByUser(userId),
                statsRepository.findMonthlyPurchaseTotalsByUser(userId)
        );
    }

    // 월별 상세 내역
    public List<MonthlyDetailRes> getMonthlyDetail(String month, Long userId) {
        List<Object[]> rechargeList = (userId != null)
                ? statsRepository.findRechargeDetailsByMonthAndUser(month, userId)
                : statsRepository.findRechargeDetailsByMonth(month);

        List<Object[]> purchaseList = (userId != null)
                ? statsRepository.findPurchaseDetailsByMonthAndUser(month, userId)
                : statsRepository.findPurchaseDetailsByMonth(month);

        List<MonthlyDetailRes> result = new ArrayList<>();

        // 충전 내역
        rechargeList.forEach(r -> result.add(MonthlyDetailRes.builder()
                .type("RECHARGE")
                .date((String) r[0])
                .nickname((String) r[1])
                .itemName(null)
                .amount(((Number) r[2]).longValue())
                .createdAt((String) r[0])
                .build()));

        // 구매 내역
        purchaseList.forEach(p -> result.add(MonthlyDetailRes.builder()
                .type("PURCHASE")
                .date((String) p[0])
                .nickname((String) p[1])
                .itemName((String) p[2])
                .amount(((Number) p[3]).longValue())
                .createdAt((String) p[0])
                .build()));

        // 최신순 정렬
        result.sort(Comparator.comparing(MonthlyDetailRes::getDate).reversed());
        return result;
    }

    // TOP 10 충전, 구매
    public List<UserSpendTopRes> getTopRechargeUsers() {
        return statsRepository.findTopRechargeUsers().stream()
                .map(rank -> UserSpendTopRes.builder()
                        .userId(((Number) rank[0]).longValue())
                        .nickname((String) rank[1])
                        .totalAmount(((Number) rank[2]).longValue())
                        .type("RECHARGE")
                        .build())
                .collect(Collectors.toList());
    }

    public List<UserSpendTopRes> getTopPurchaseUsers() {
        return statsRepository.findTopPurchaseUsers().stream()
                .map(rank -> UserSpendTopRes.builder()
                        .userId(((Number) rank[0]).longValue())
                        .nickname((String) rank[1])
                        .totalAmount(((Number) rank[2]).longValue())
                        .type("PURCHASE")
                        .build())
                .collect(Collectors.toList());
    }

    // 인기 아이템 조회
    public List<PopularItemRes> getPopularItems() {
        return statsRepository.findPopularItems().stream()
                .map(result -> PopularItemRes.builder()
                        .pointItemName((String) result[0])
                        .purchaseCount(((Number) result[1]).intValue())
                        .build())
                .toList();
    }

    // 상위 사용자 조회
    public List<UserSpendTopRes> getTopUsers() {
        return statsRepository.findTopUsersByTotalSpentPoints().stream()
                .map(result -> UserSpendTopRes.builder()
                        .userId(((Number) result[0]).longValue())
                        .totalAmount(((Number) result[1]).longValue())
                        .type("PURCHASE")
                        .build())
                .toList();
    }

    // 공통 병합 로직
    private List<MonthlySummaryStatsRes> mergeStats(List<Object[]> rechargeData, List<Object[]> purchaseData) {
        Map<String, Long> rechargeMap = rechargeData.stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> ((Number) r[1]).longValue()
                ));

        Map<String, Long> purchaseMap = purchaseData.stream()
                .collect(Collectors.toMap(
                        p -> (String) p[0],
                        p -> ((Number) p[1]).longValue()
                ));

        Set<String> allMonths = new HashSet<>();
        allMonths.addAll(rechargeMap.keySet());
        allMonths.addAll(purchaseMap.keySet());

        return allMonths.stream()
                .sorted()
                .map(month -> {
                    long recharge = rechargeMap.getOrDefault(month, 0L);
                    long purchase = purchaseMap.getOrDefault(month, 0L);
                    return MonthlySummaryStatsRes.builder()
                            .month(month)
                            .totalRecharge(recharge)
                            .totalPurchase(purchase)
                            .netChange(recharge - purchase)
                            .build();
                })
                .toList();
    }
}
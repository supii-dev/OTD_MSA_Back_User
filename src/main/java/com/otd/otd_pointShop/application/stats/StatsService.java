package com.otd.otd_pointShop.application.stats;

import com.otd.otd_pointShop.application.stats.model.MonthlyStatsRes;
import com.otd.otd_pointShop.application.stats.model.UserSpendTopRes;
import com.otd.otd_pointShop.application.stats.model.PopularItemRes;
import com.otd.otd_pointShop.repository.PurchaseHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;

    public List<MonthlyStatsRes> getMonthlyStats() {
        return purchaseHistoryRepository.findMonthlySpentPoints().stream()
                .map(obj -> MonthlyStatsRes.builder()
                        .month((String) obj[0])
                        .totalSpentPoints(((Number) obj[1]).intValue())
                        .build())
                .collect(Collectors.toList());
    }

    public List<UserSpendTopRes> getTopSpenders() {
        return purchaseHistoryRepository.findTopUsersByTotalSpentPoints().stream()
                .map(obj -> UserSpendTopRes.builder()
                        .userId(((Number) obj[0]).longValue())
                        .totalSpentPoints(((Number) obj[1]).intValue())
                        .build())
                .collect(Collectors.toList());
    }

    public List<PopularItemRes> getPopularItems() {
        return purchaseHistoryRepository.findTopPurchasedItems().stream()
                .map(obj -> PopularItemRes.builder()
                        .itemName((String) obj[0])
                        .purchaseCount(((Number) obj[1]).intValue())
                        .build())
                .collect(Collectors.toList());
    }
}

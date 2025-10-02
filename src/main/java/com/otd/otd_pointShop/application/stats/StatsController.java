package com.otd.otd_pointShop.application.stats;

import com.otd.otd_pointShop.application.stats.model.MonthlyStatsRes;
import com.otd.otd_pointShop.application.stats.model.PopularItemRes;
import com.otd.otd_pointShop.application.stats.model.UserSpendTopRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pointshop/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/monthly")
    public List<MonthlyStatsRes> getMonthlyStats() {
        return statsService.getMonthlyStats();
    }

    @GetMapping("/top-users")
    public List<UserSpendTopRes> getTopUsers() {
        return statsService.getTopSpenders();
    }

    @GetMapping("/popular-items")
    public List<PopularItemRes> getPopularItems() {
        return statsService.getPopularItems();
    }
}

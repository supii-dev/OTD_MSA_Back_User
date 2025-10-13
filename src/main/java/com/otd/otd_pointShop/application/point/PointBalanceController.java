package com.otd.otd_pointShop.application.point;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/OTD/pointshop/balance")
@RequiredArgsConstructor
public class PointBalanceController {
    private final PointBalanceService pointBalanceService;

    @PostMapping("/create")
    public String create(@RequestParam Long userId, @RequestParam int amount) {
        pointBalanceService.createBalance(userId, amount);
        return "Balance created for user " + userId;
    }
}
package com.otd.otd_pointShop.application.purchase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;

@Service
@RequiredArgsConstructor
public class RedisPointBalanceService implements PointBalanceService {

    private final StringRedisTemplate redisTemplate;
    private static final String POINT_KEY_PREFIX = "point:user:";

    private String getKey(Long userId) {
        return POINT_KEY_PREFIX + userId;
    }

    @Override
    public Integer getPointBalance(Long userId) {
        String balance = redisTemplate.opsForValue().get(getKey(userId));
        return balance != null ? Integer.parseInt(balance) : 0;
    }

    @Override
    public void pointIncrement(Long userId, int amount) {
        redisTemplate.opsForValue().increment(getKey(userId), amount);
    }

    @Override
    public void pointDecrement(Long userId, int amount) {
        redisTemplate.opsForValue().decrement(getKey(userId), amount);
    }
}

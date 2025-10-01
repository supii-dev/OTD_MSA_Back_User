package com.otd.otd_pointShop.application.point;

import com.otd.otd_user.application.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointUserService {

    private final UserRepository userRepository;

    public int getPointBalance(Long userId) {
        return userRepository.findPointByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
    }

    // 포인트 충전
    @Transactional
    public void chargePoint(Long userId, int amount) {
        userRepository.updatePointByUserId(amount, userId);
    }

    // 포인트 차감
    @Transactional
    public void deductPoint(Long userId, int amount) {

        // 유저의 포인트 조회
        int current = getPointBalance(userId);
        if (current < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        userRepository.updatePointByUserId(-amount, userId);
    }
}
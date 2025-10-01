package com.otd.otd_pointShop.application.point;

import com.otd.otd_user.application.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointUserService {

    private final UserRepository userRepository;

    public int getPointBalance(Integer userId) {
        return userRepository.findPointByUserId(userId.longValue())
                .orElseThrow(() -> new RuntimeException("사용자 정보가 없습니다."));
    }
}
package com.otd.otd_pointShop.application.point;

import com.otd.configuration.model.ResultResponse;
import com.otd.otd_pointShop.application.point.model.PointApiResponse;
import com.otd.otd_pointShop.application.point.model.PointBalanceResponse;
import com.otd.otd_pointShop.application.point.model.PointChargeRequest;
import com.otd.otd_pointShop.entity.PointUser;
import com.otd.otd_pointShop.repository.PointUserRepository;
import com.otd.otd_user.application.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/OTD/pointshop/user")
@RequiredArgsConstructor
public class PointUserController {

    private final PointUserService pointUserService;
    private final UserRepository userRepository;
    private final PointUserRepository pointUserRepository;

    // 포인트 충전
    @PostMapping("/charge")
    public ResponseEntity<String> chargePoint(HttpSession session, @RequestBody PointChargeRequest req) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        pointUserService.chargePoint(userId.longValue(), req.getAmount());
        return ResponseEntity.ok("포인트 충전 완료");
    }

    // 포인트 수정, 차감
    @PostMapping("/deduct")
    public ResponseEntity<String> deductPoint(HttpSession session, @RequestBody PointChargeRequest req) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            pointUserService.deductPoint(userId.longValue(), req.getAmount());
            return ResponseEntity.ok("포인트 차감 완료");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 포인트 잔액 조회
    @GetMapping("/balance")
    public ResponseEntity<PointBalanceResponse> getBalance(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int balance = pointUserService.getPointBalance(userId.longValue());
        return ResponseEntity.ok(new PointBalanceResponse(balance));
    }

    @GetMapping("/history")
    public ResponseEntity<PointApiResponse> getUserPointHistory(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                    .body(PointApiResponse.error("로그인이 필요합니다."));
        }

        List<PointUser> history = pointUserService.getUserPointHistory(userId);
        return ResponseEntity.ok(PointApiResponse.success(history));
    }
}
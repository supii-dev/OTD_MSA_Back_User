package com.otd.otd_pointShop.application.point;

import com.otd.otd_pointShop.application.point.model.PointBalanceResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/OTD/pointshop/user")
@RequiredArgsConstructor
public class PointUserController {

    private final PointUserService pointUserService;

    @GetMapping("/points")
    public ResponseEntity<PointBalanceResponse> getUserPoint(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int point = pointUserService.getPointBalance(userId);
        return ResponseEntity.ok(new PointBalanceResponse(point));
    }
}


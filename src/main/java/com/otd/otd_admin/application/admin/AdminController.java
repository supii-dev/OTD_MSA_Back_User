package com.otd.otd_admin.application.admin;

import com.otd.otd_admin.application.admin.model.AdminUserDetailGetRes;
import com.otd.otd_admin.application.admin.model.AdminUserGetRes;
import com.otd.otd_admin.application.admin.model.AgeCountRes;
import com.otd.otd_admin.application.admin.model.GenderCountRes;
import com.otd.otd_challenge.entity.ChallengeDefinition;
import com.otd.otd_challenge.entity.ChallengePointHistory;
import com.otd.otd_user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/OTD/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/user")
    public List<User> getUsers() {
        return adminService.getUsers();
    }

    @GetMapping("/challenge")
    public List<ChallengeDefinition> getChallenges() {
        return adminService.getChallenges();
    }

    @GetMapping("/point")
    public List<ChallengePointHistory> getPointHistory() {
        return adminService.getPointHistory();
    }

    @GetMapping("user/{userId}")
    public AdminUserDetailGetRes getUserDetail(@PathVariable Long userId) {
        return adminService.getUserDetail(userId);
    }











    // 대시보드쪽
    @GetMapping("/gender")
    public List<GenderCountRes> getGenderCount() {
        return adminService.getGenderCount();
    }

    @GetMapping("/agegroup")
    public List<AgeCountRes> getAgeGroup() {
        return adminService.getAgeCount();
    }
}

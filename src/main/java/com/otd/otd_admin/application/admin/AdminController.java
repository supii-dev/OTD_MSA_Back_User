package com.otd.otd_admin.application.admin;

import com.otd.configuration.model.ResultResponse;
import com.otd.otd_admin.application.admin.model.*;
import com.otd.otd_challenge.entity.ChallengeDefinition;
import com.otd.otd_challenge.entity.ChallengePointHistory;
import com.otd.otd_user.entity.Inquiry;
import com.otd.otd_user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/OTD/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    // 유저
    @GetMapping("/user")
    public List<User> getUsers() {
        return adminService.getUsers();
    }

    @GetMapping("user/{userId}")
    public AdminUserDetailGetRes getUserDetail(@PathVariable Long userId) {
        return adminService.getUserDetail(userId);
    }

    @PutMapping("/user/modify")
    public ResultResponse<?> putUser(@RequestBody AdminUserPutReq req){
        return adminService.putUserDetail(req);
    }

    // 챌린지
    @GetMapping("/challenge")
    public List<ChallengeDefinition> getChallenges() {
        return adminService.getChallenges();
    }

    @PutMapping("/challenge/modify")
    public ResultResponse<?> putChallenge(@RequestBody AdminChallengePutReq req){
        return adminService.putChallengeDetail(req);
    }

    @DeleteMapping("/user/{userId}")
    public ResultResponse<?> deleteUser(@PathVariable Long userId){
        return adminService.removeUser(userId);
    }
    @DeleteMapping("/challenge/{cdId}")
    public ResultResponse<?> deleteChallenge(@PathVariable Long cdId) {
        return adminService.removeChallenge(cdId);
    }

    // 포인트
    @GetMapping("/point")
    public List<ChallengePointHistory> getPointHistory() {
        return adminService.getPointHistory();
    }

    // 통계
    @GetMapping("/gender")
    public List<GenderCountRes> getGenderCount() {
        return adminService.getGenderCount();
    }

    @GetMapping("/agegroup")
    public List<AgeCountRes> getAgeGroup() {
        return adminService.getAgeCount();
    }

    @GetMapping("/tier")
    public List<TierCountRes> getTierCount() {
        return adminService.getTierCount();
    }

    @GetMapping("challengerate")
    public List<ChallengeSuccessRateCountRes> getChallengeSuccessRateCount() {
        return adminService.getChallengeSuccessRateCount();
    }

    // 문의
    @GetMapping("/qna")
    public List<Inquiry> getInquiry() {
        return adminService.getInquiry();
    }
}

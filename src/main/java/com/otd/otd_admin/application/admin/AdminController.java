package com.otd.otd_admin.application.admin;

import com.otd.configuration.model.ResultResponse;
import com.otd.otd_admin.application.admin.model.*;
import com.otd.otd_challenge.entity.ChallengeDefinition;
import com.otd.otd_challenge.entity.ChallengePointHistory;
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

    @GetMapping("/users")
    public List<User> getUsers() {
        return adminService.getUsers();
    }

    @GetMapping("/challenges")
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

    @PutMapping("/{userId}")
    public ResultResponse<?> putUser(@PathVariable Long userId,
                                     @RequestBody AdminUserPutReq req){
        req.setUserId(userId);
        return adminService.putUserDetail(req);
    }

    @PutMapping("/{cdId}")
    public ResultResponse<?> putChallenge(@PathVariable Long cdId,
                                          @RequestBody AdminChallengePutReq req){
        req.setCdId(cdId);
        return adminService.putChallengeDetail(req);
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

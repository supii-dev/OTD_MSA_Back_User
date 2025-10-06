package com.otd.otd_admin.application.admin;

import com.otd.configuration.enumcode.model.EnumChallengeRole;
import com.otd.configuration.enumcode.model.EnumUserRole;
import com.otd.otd_admin.application.admin.Repository.AdminPointRepository;
import com.otd.otd_admin.application.admin.Repository.AdminUserRepository;
import com.otd.otd_admin.application.admin.model.AdminUserDetailGetRes;
import com.otd.otd_admin.application.admin.model.AdminUserGetRes;
import com.otd.otd_admin.application.admin.model.AgeCountRes;
import com.otd.otd_admin.application.admin.model.GenderCountRes;
import com.otd.otd_challenge.application.challenge.ChallengeMapper;
import com.otd.otd_challenge.application.challenge.Repository.ChallengeDefinitionRepository;
import com.otd.otd_challenge.application.challenge.Repository.ChallengePointRepository;
import com.otd.otd_challenge.application.challenge.Repository.ChallengeProgressRepository;
import com.otd.otd_challenge.entity.ChallengeDefinition;
import com.otd.otd_challenge.entity.ChallengePointHistory;
import com.otd.otd_challenge.entity.ChallengeProgress;
import com.otd.otd_user.application.user.UserMapper;
import com.otd.otd_user.application.user.UserRepository;
import com.otd.otd_user.entity.User;
import com.otd.otd_user.entity.UserRoleIds;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserMapper userMapper;

    private final ChallengeMapper challengeMapper;
    private final ChallengeProgressRepository challengeProgressRepository;
    private final ChallengeDefinitionRepository challengeDefinitionRepository;
    private final ChallengePointRepository challengePointRepository;

    private final AdminMapper adminMapper;
    private final AdminUserRepository adminUserRepository;
    private final AdminPointRepository adminPointRepository;
    private final UserRepository userRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public List<ChallengeDefinition> getChallenges() {
        return challengeDefinitionRepository.findAll();
    }

    public List<ChallengePointHistory> getPointHistory() {
        return adminPointRepository.findAll();
    }

    public List<GenderCountRes> getGenderCount() {
        return adminUserRepository.countUserByGender();
    }

    public List<AgeCountRes> getAgeCount() {
        return adminMapper.groupByAge();
    }

    public AdminUserDetailGetRes getUserDetail(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<ChallengeProgress> cp = challengeProgressRepository.findByUserId(user.getUserId());
        List<ChallengePointHistory> ch = challengePointRepository.findByUserId(user.getUserId());

        return AdminUserDetailGetRes.builder().
            challengeProgress(cp).challengePointHistory(ch).build();
    }
}

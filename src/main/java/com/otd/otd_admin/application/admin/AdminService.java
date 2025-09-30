package com.otd.otd_admin.application.admin;

import com.otd.otd_admin.application.admin.Repository.AdminPointRepository;
import com.otd.otd_admin.application.admin.Repository.AdminUserRepository;
import com.otd.otd_admin.application.admin.model.AgeCountRes;
import com.otd.otd_admin.application.admin.model.GenderCountRes;
import com.otd.otd_challenge.application.challenge.ChallengeMapper;
import com.otd.otd_challenge.application.challenge.Repository.ChallengeDefinitionRepository;
import com.otd.otd_challenge.entity.ChallengeDefinition;
import com.otd.otd_challenge.entity.ChallengePointHistory;
import com.otd.otd_user.application.user.UserMapper;
import com.otd.otd_user.entity.User;
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
    private final AdminMapper adminMapper;
    private final AdminUserRepository adminUserRepository;
    private final ChallengeDefinitionRepository challengeDefinitionRepository;
    private final AdminPointRepository adminPointRepository;

    public List<User> getUsers() {
        return adminUserRepository.findAll();
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
}

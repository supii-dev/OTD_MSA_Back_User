package com.otd.otd_admin.application.admin;

import com.otd.configuration.enumcode.model.EnumChallengeRole;
import com.otd.configuration.enumcode.model.EnumUserRole;
import com.otd.configuration.feignclient.LifeFeignClient;
import com.otd.configuration.model.ResultResponse;
import com.otd.otd_admin.application.admin.Repository.AdminInquiryRepository;
import com.otd.otd_admin.application.admin.Repository.AdminPointRepository;
import com.otd.otd_admin.application.admin.Repository.AdminUserRepository;
import com.otd.otd_admin.application.admin.model.*;
import com.otd.otd_challenge.application.challenge.ChallengeMapper;
import com.otd.otd_challenge.application.challenge.Repository.*;
import com.otd.otd_challenge.entity.ChallengeDefinition;
import com.otd.otd_challenge.entity.ChallengePointHistory;
import com.otd.otd_challenge.entity.ChallengeProgress;
import com.otd.otd_user.application.email.InquiryRepository;
import com.otd.otd_user.application.user.UserMapper;
import com.otd.otd_user.application.user.UserRepository;
import com.otd.otd_user.application.user.model.UserRoleRepository;
import com.otd.otd_user.entity.Inquiry;
import com.otd.otd_user.entity.User;
import com.otd.otd_user.entity.UserRole;
import com.otd.otd_user.entity.UserRoleIds;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    private final UserRoleRepository userRoleRepository;
    private final AdminMapper adminMapper;
    private final AdminUserRepository adminUserRepository;
    private final AdminPointRepository adminPointRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminInquiryRepository adminInquiryRepository;
    private final InquiryRepository inquiryRepository;
    private final ChallengeMissionRepository challengeMissionRepository;
    private final ChallengeSettlementRepository challengeSettlementRepository;
    private final LifeFeignClient lifeFeignClient;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public List<ChallengeDefinition> getChallenges() {
        return challengeDefinitionRepository.findAll();
    }

    public List<ChallengePointHistory> getPointHistory() {
        return adminPointRepository.findAll();
    }

    public List<Inquiry> getInquiry() {
        return adminInquiryRepository.findAll();
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

    @Transactional
    public ResultResponse<?> putUserDetail(AdminUserPutReq req){
        User user = userRepository.findByUserId(req.getUserId());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다.");
        }
        user.setName(req.getName());
        user.setNickName(req.getNickName());
        user.setPoint(req.getPoint());
        user.setXp(req.getXp());
        user.setUid(req.getUid());
        user.setPic(req.getPic());
        if (req.getUpw() != null && !req.getUpw().isBlank()) {
            user.setUpw(passwordEncoder.encode(req.getUpw()));
        }
        // 기존 역할 조회
        UserRole exist = userRoleRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user_role 정보가 없습니다."));

        EnumUserRole newRole = req.getUserRoles();
        EnumChallengeRole newChallenge = req.getChallengeRole();

        EnumUserRole currentRole = exist.getUserRoleIds().getRoleCode();
        EnumChallengeRole currentChallenge = exist.getUserRoleIds().getChallengeCode();

        // 값이 다르면 삭제 후 새로 추가
        if (!currentRole.equals(newRole) || !currentChallenge.equals(newChallenge)) {
            user.getUserRoles().remove(exist);   // 관계 제거
            userRoleRepository.delete(exist);

            UserRoleIds ids = new UserRoleIds(user.getUserId(), newRole, newChallenge);
            UserRole newUserRole = new UserRole(ids, user);
            user.getUserRoles().add(newUserRole);
            userRoleRepository.save(newUserRole);
        }

        userRepository.save(user);
        return new ResultResponse<>("유저 정보가 수정되었습니다.", user.getUserId());
    }

    @Transactional
    public ResultResponse<?> putChallengeDetail(AdminChallengePutReq req) {
        ChallengeDefinition cd = challengeDefinitionRepository.findByCdId(req.getCdId());
        if (cd == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 챌린지를 찾을 수 없습니다.");
        }
        cd.setNote(req.getNote());
        cd.setCdGoal(req.getCdGoal());
        cd.setCdName(req.getCdName());
        cd.setCdImage(req.getCdImage());
        cd.setCdReward(req.getCdReward());
        cd.setCdType(req.getCdType());
        cd.setCdUnit(req.getCdUnit());
        cd.setXp(req.getXp());
        cd.setTier(req.getTier());
        return new ResultResponse<>("챌린지 정보가 수정되었습니다.", req.getCdId());
    }

    @Transactional
    public ResultResponse<?> removeUser(Long userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "유저 없음");
        }

        // life 서버에 삭제 요청
        ResultResponse<?> lifeResponse = lifeFeignClient.deleteUserData(userId);

        // cascade로 묶여있는 관련 데이터까지 삭제
        userRepository.delete(user);

        return new ResultResponse<>(
                "유저 삭제 완료 (Life 서버 응답: " + lifeResponse.getMessage() + ")", userId
        );
    }

    @Transactional
    public ResultResponse<?> removeChallenge(Long cdId) {
        int result = challengeDefinitionRepository.deleteByCdId(cdId);
        if (result == 1) {
            return new ResultResponse<>("챌린지 삭제가 되었습니다.", result);
        } else {
            return new ResultResponse<>("챌린지 삭제에 실패하였습니다.", result);
        }
    }
}

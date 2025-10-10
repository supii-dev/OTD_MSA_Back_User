package com.otd.otd_admin.application.admin;

import com.otd.configuration.enumcode.model.EnumChallengeRole;
import com.otd.configuration.enumcode.model.EnumUserRole;
import com.otd.configuration.feignclient.LifeFeignClient;
import com.otd.configuration.model.ResultResponse;
import com.otd.configuration.util.MyFileManager;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
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
    private final MyFileManager myFileManager;
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

    public List<TierCountRes> getTierCount() {
        return adminMapper.countByTier();
    }

    public List<ChallengeSuccessRateCountRes> getChallengeSuccessRateCount() {
        return adminMapper.countByChallengeType();
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
    public AdminChallengeDto addChallenge(AdminChallengeDto dto) {
        ChallengeDefinition cd = ChallengeDefinition.builder()
                .cdName(dto.getCdName())
                .cdType(dto.getCdType())
                .cdGoal(dto.getCdGoal())
                .cdUnit(dto.getCdUnit())
                .cdReward(dto.getCdReward())
                .xp(dto.getXp())
                .tier(dto.getTier())
                .cdImage(dto.getCdImage())
                .build();

        challengeDefinitionRepository.save(cd);
        dto.setCdId(cd.getCdId());
        return dto;
    }

    public String saveChallengeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일이 없습니다");
        }
        return myFileManager.saveChallengeImage(file);
    }

    @Transactional
    public AdminChallengeDto modifyChallenge(AdminChallengeDto dto) {
        ChallengeDefinition cd = challengeDefinitionRepository.findByCdId(dto.getCdId());
        if (cd == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 챌린지입니다");
        }
        cd.update(dto);
        return dto;
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

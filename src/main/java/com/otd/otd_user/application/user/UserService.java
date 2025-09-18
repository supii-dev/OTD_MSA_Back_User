package com.otd.otd_user.application.user;

import com.otd.otd_user.application.email.EmailService;
import com.otd.otd_user.application.email.model.PasswordChangeReq;
import com.otd.otd_user.application.email.model.PasswordResetReq;
import com.otd.otd_user.application.user.model.*;
import com.otd.configuration.enumcode.model.EnumUserRole;
import com.otd.configuration.model.JwtUser;
import com.otd.configuration.security.SignInProviderType;
import com.otd.configuration.util.ImgUploadManager;
import com.otd.otd_user.entity.User;
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
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImgUploadManager imgUploadManager;
    private final EmailService emailService;


    @Transactional
    public void join(UserJoinReq req, MultipartFile pic) {
        // 1. 이메일 인증 확인 (EmailService 사용)
        if (!emailService.isEmailVerified(req.getEmail())) {
            throw new IllegalArgumentException("이메일 인증을 완료해주세요.");
        }

        // 2. 중복 검사들 (기존 메서드 사용)
        if (!isUidAvailable(req.getUid())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (req.getNickName() != null && !isNicknameAvailable(req.getNickName())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 3. 비밀번호 해싱
        String hashedPassword = passwordEncoder.encode(req.getUpw());

        // 4. User 엔티티 생성 및 저장
        User user = new User();
        user.setProviderType(SignInProviderType.LOCAL);
        user.setUid(req.getUid());
        user.setUpw(hashedPassword);
        user.setNickName(req.getNickName());
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setPhoneNumber(req.getPhone());
        user.setBirthDate(req.getBirthDate());
        user.setGender(req.getGender());
//        user.setCi(req.getCi());
//        user.setDi(req.getDi());

        // 기본 역할 설정
        if (req.getRoles() == null || req.getRoles().isEmpty()) {
            user.addUserRoles(List.of(EnumUserRole.USER));
        } else {
            user.addUserRoles(req.getRoles());
        }

        User savedUser = userRepository.save(user);

        // 5. 프로필 사진 처리
        if (pic != null && !pic.isEmpty()) {
            String savedFileName = imgUploadManager.saveProfilePic(savedUser.getUserId(), pic);
            savedUser.setPic(savedFileName);
        }

        // 6. 이메일 인증 상태 삭제 (EmailService 사용)
        emailService.removeEmailVerificationStatus(req.getEmail());

        log.info("회원가입 완료: 사용자 ID {}", savedUser.getUserId());
    }

    public UserLoginDto login(UserLoginReq req) {
        User user = userRepository.findByUidAndProviderType(req.getUid(), SignInProviderType.LOCAL);
        if(user == null || !passwordEncoder.matches(req.getUpw(), user.getUpw())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디/비밀번호를 확인해 주세요.");
        }

        List<EnumUserRole> roles = user.getUserRoles().stream()
                .map(item -> item.getUserRoleIds().getRoleCode()).toList();

        log.info("roles: {}", roles);
        JwtUser jwtUser = new JwtUser(user.getUserId(), roles);

        UserLoginRes userLoginRes = UserLoginRes.builder()
                .userId(user.getUserId())
                .nickName(user.getNickName() == null ? user.getUid() : user.getNickName())
                .pic(user.getPic())
                .build();

        return UserLoginDto.builder()
                .jwtUser(jwtUser)
                .userLoginRes(userLoginRes)
                .build();
    }

    //비밀번호 재설정(비밀번호 찾기)
    @Transactional
    public void resetPassword(PasswordResetReq req) {
        // 1. 비밀번호 재설정 권한 확인
        if (!emailService.canResetPassword(req.getEmail())) {
            throw new IllegalArgumentException("비밀번호 재설정 권한이 없습니다. 이메일 인증을 먼저 완료해주세요.");
        }

        // 2. 사용자 조회
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        // 3. 새 비밀번호 해싱 및 저장
        String hashedPassword = passwordEncoder.encode(req.getNewPassword());
        user.setUpw(hashedPassword);

        // 4. 비밀번호 재설정 권한 삭제
        emailService.removePasswordResetPermission(req.getEmail());

        log.info("비밀번호 재설정 완료: {}", req.getEmail());
    }
    @Transactional
    public void changePassword(long userId, PasswordChangeReq req) {
        // 새 비밀번호와 확인 비밀번호 일치 확인
        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getUpw())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        // 새 비밀번호 해싱 및 저장
        String hashedPassword = passwordEncoder.encode(req.getNewPassword());
        user.setUpw(hashedPassword);

        log.info("비밀번호 변경 완료: 사용자 ID {}", userId);
    }


    public boolean isUidAvailable(String uid) {
        return userMapper.countByUid(uid) == 0;
    }

    public boolean isNicknameAvailable(String nickname) {
        return userMapper.countByNickname(nickname) == 0;
    }

    public boolean isDuplicateUser(String ci, String di) {
        int count = 0;
        if (ci != null && !ci.isEmpty()) {
            count += userMapper.countByCi(ci);
        }
        if (di != null && !di.isEmpty()) {
            count += userMapper.countByDi(di);
        }
        return count > 0;
    }

    public UserProfileGetRes getProfileUser(UserProfileGetDto dto) {
        // MyBatis 사용 (복잡한 조회)
        return userMapper.findProfileByUserId(dto);
    }

    @Transactional
    public String patchProfilePic(long signedUserId, MultipartFile pic) {
        User user = userRepository.findById(signedUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다."));
        imgUploadManager.removeProfileDirectory(signedUserId);
        String savedFileName = imgUploadManager.saveProfilePic(signedUserId, pic);
        user.setPic(savedFileName);
        return savedFileName;
    }

    @Transactional
    public void deleteProfilePic(long signedUserId) {
        User user = userRepository.findById(signedUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다."));
        imgUploadManager.removeProfileDirectory(signedUserId);
        user.setPic(null);
    }

}
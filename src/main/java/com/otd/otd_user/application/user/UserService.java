package com.otd.otd_user.application.user;

import com.otd.otd_user.application.user.model.*;
import com.otd.otd_user.configuration.enumcode.model.EnumUserRole;
import com.otd.otd_user.configuration.model.JwtUser;
import com.otd.otd_user.configuration.security.SignInProviderType;
import com.otd.otd_user.configuration.util.ImgUploadManager;
import com.otd.otd_user.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImgUploadManager imgUploadManager;
    private final RedisTemplate<String, String> redisTemplate; // Redis 사용시
    private final JavaMailSender mailSender; // 이메일 발송용

    @Transactional
    public void signUp(UserSignUpReq req, MultipartFile pic) {
        if (!isEmailVerified(req.getEmail())) {
            throw new IllegalArgumentException("이메일 인증을 완료해주세요.");
        }
        String hashedPassword = passwordEncoder.encode(req.getUpw());

        User user = new User();
        user.setProviderType(SignInProviderType.LOCAL);
        user.setNickName(req.getNickName());
        user.setUid(req.getUid());
        user.setUpw(hashedPassword);
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setBirthDate(req.getBirthDate());
        user.setGender(req.getGender());
        user.addUserRoles(req.getRoles());

        userRepository.save(user);

        if (pic != null) {
            String savedFileName = imgUploadManager.saveProfilePic(user.getUserId(), pic);
            user.setPic(savedFileName);
        }
        String verifiedKey = "email_verified:" + req.getEmail();
        redisTemplate.delete(verifiedKey);

    }

    public UserSignInDto signIn(UserSignInReq req) {
        User user = userRepository.findByUidAndProviderType(req.getUid(), SignInProviderType.LOCAL); //일치하는 아이디가 있는지 확인, null이 넘어오면 uid가 없음
        //passwordEncoder 내부에는 jbcrypt 객체가 있다.
        if(user == null || !passwordEncoder.matches(req.getUpw(), user.getUpw())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "아이디/비밀번호를 확인해 주세요.");
        }
        //user 튜플을 가져왔는데 user_role에 저장되어 있는 데이터까지 가져올 수 있었던건 양방향 관계 설정을 했기 때문에 가능
        //Fetch = FatchType.LAZY였을 때 user.getUserRoles()는 JPA 그래프 탐색(SELECT가 날아간다.)이라고 칭한다.

//        List<UserRole> userRoles2 = user.getUserRoles();
//        List<EnumUserRole> resultList = new ArrayList<>(userRoles2.size());
//        for(UserRole role : userRoles2) {
//            resultList.add(role.getUserRoleIds().getRoleCode());
//        }

        List<EnumUserRole> roles = user.getUserRoles().stream().map(item -> item.getUserRoleIds().getRoleCode()).toList();

        log.info("roles: {}", roles);
        JwtUser jwtUser = new JwtUser(user.getUserId(), roles);


        UserSignInRes userSignInRes = UserSignInRes.builder()
                .userId(user.getUserId()) //프로필 사진 표시 때 사용
                .nickName(user.getNickName() == null ? user.getUid() : user.getNickName())
                .pic(user.getPic()) //프로필 사진 표시 때 사용
                .build();

        return UserSignInDto.builder()
                            .jwtUser(jwtUser) //토큰 제작에 필요
                            .userSignInRes(userSignInRes) //FE에게 전달할 데이터
                            .build();
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
    // 이메일 인증코드 발송
    public void sendEmailVerificationCode(String email) {
        // 1. 이메일 중복 체크 (선택사항)
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 2. 6자리 랜덤 인증코드 생성
        String verificationCode = generateRandomCode();

        // 3. Redis에 인증코드 저장 (5분 TTL)
        String redisKey = "email_verification:" + email;
        redisTemplate.opsForValue().set(redisKey, verificationCode, Duration.ofMinutes(5));

        // 4. 이메일 발송
        sendVerificationEmail(email, verificationCode);

        log.info("이메일 인증코드 발송 완료: {}", email);
    }

    // 이메일 인증코드 확인
    public boolean verifyEmailCode(String email, String code) {
        String redisKey = "email_verification:" + email;
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            log.warn("인증코드가 만료되었거나 존재하지 않습니다: {}", email);
            return false;
        }

        if (storedCode.equals(code)) {
            // 인증 성공 시 Redis에서 삭제
            redisTemplate.delete(redisKey);

            // 인증 완료 상태를 Redis에 저장 (30분 TTL)
            String verifiedKey = "email_verified:" + email;
            redisTemplate.opsForValue().set(verifiedKey, "true", Duration.ofMinutes(30));

            log.info("이메일 인증 성공: {}", email);
            return true;
        }

        log.warn("인증코드 불일치: {}", email);
        return false;
    }

    // 이메일 인증 상태 확인 (회원가입 시 사용)
    public boolean isEmailVerified(String email) {
        String verifiedKey = "email_verified:" + email;
        String verified = redisTemplate.opsForValue().get(verifiedKey);
        return "true".equals(verified);
    }

    // 6자리 랜덤 코드 생성
    private String generateRandomCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    // 인증 이메일 발송
    private void sendVerificationEmail(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[OneToDay] 이메일 인증코드");
            helper.setText(createEmailContent(code), true);
            helper.setFrom("hwangsubin93@gmail.com"); // 발신자 이메일

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("이메일 발송 실패: {}", email, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }
    }

    // 이메일 HTML 템플릿
    private String createEmailContent(String code) {
        return """
            <div style="max-width: 600px; margin: 0 auto; padding: 20px; font-family: Arial, sans-serif;">
                <div style="text-align: center; margin-bottom: 30px;">
                    <h1 style="color: #10b981;">GreenGram</h1>
                    <h2 style="color: #374151;">이메일 인증</h2>
                </div>
                
                <div style="background-color: #f9fafb; padding: 30px; border-radius: 8px; text-align: center;">
                    <p style="font-size: 16px; color: #6b7280; margin-bottom: 20px;">
                        아래 인증코드를 입력하여 이메일 인증을 완료해주세요.
                    </p>
                    
                    <div style="background-color: #10b981; color: white; font-size: 32px; font-weight: bold; 
                                padding: 20px; border-radius: 8px; letter-spacing: 8px; margin: 20px 0;">
                        %s
                    </div>
                    
                    <p style="font-size: 14px; color: #9ca3af;">
                        인증코드는 5분간 유효합니다.
                    </p>
                </div>
                
                <div style="text-align: center; margin-top: 30px;">
                    <p style="font-size: 12px; color: #9ca3af;">
                        본 이메일은 발신전용입니다. 문의사항이 있으시면 고객센터로 연락해주세요.
                    </p>
                </div>
            </div>
            """.formatted(code);
    }
}

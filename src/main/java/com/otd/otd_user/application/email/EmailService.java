package com.otd.otd_user.application.email;

import com.otd.otd_user.application.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    private static final int CODE_EXPIRY_MINUTES = 5;
    private static final int VERIFICATION_STATUS_EXPIRY_MINUTES = 30;

    /**
     * 회원가입용 이메일 인증코드 발송
     */
    public void sendEmailVerificationCode(String email) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 2. 인증코드 생성 및 발송
        String verificationCode = generateRandomCode();
        saveVerificationCode(email, verificationCode);
        sendVerificationEmail(email, verificationCode);

        log.info("회원가입용 이메일 인증코드 발송 완료: {}", email);
    }

    /**
     * 비밀번호 재설정용 이메일 인증코드 발송
     */
    public void sendPasswordResetCode(String email) {
        // 1. 이메일 존재 여부 체크
        if (!userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("등록되지 않은 이메일입니다.");
        }

        // 2. 인증코드 생성 및 발송
        String resetCode = generateRandomCode();
        savePasswordResetCode(email, resetCode);
        sendPasswordResetEmail(email, resetCode);

        log.info("비밀번호 재설정용 이메일 인증코드 발송 완료: {}", email);
    }

    /**
     * 이메일 인증코드 검증 (회원가입용)
     */
    public boolean verifyEmailCode(String email, String code) {
        String redisKey = "email_verification:" + email;
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            log.warn("인증코드가 만료되었거나 존재하지 않습니다: {}", email);
            return false;
        }

        if (storedCode.equals(code)) {
            // 인증 성공 시 기존 코드 삭제
            redisTemplate.delete(redisKey);

            // 인증 완료 상태를 Redis에 저장
            String verifiedKey = "email_verified:" + email;
            redisTemplate.opsForValue().set(verifiedKey, "true",
                    Duration.ofMinutes(VERIFICATION_STATUS_EXPIRY_MINUTES));

            log.info("이메일 인증 성공: {}", email);
            return true;
        }

        log.warn("인증코드 불일치: {}", email);
        return false;
    }

    /**
     * 비밀번호 재설정 코드 검증
     */
    public boolean verifyPasswordResetCode(String email, String code) {
        String redisKey = "password_reset:" + email;
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            log.warn("비밀번호 재설정 코드가 만료되었거나 존재하지 않습니다: {}", email);
            return false;
        }

        if (storedCode.equals(code)) {
            // 인증 성공 시 기존 코드 삭제
            redisTemplate.delete(redisKey);

            // 비밀번호 재설정 권한 부여 (10분간 유효)
            String resetTokenKey = "password_reset_verified:" + email;
            redisTemplate.opsForValue().set(resetTokenKey, "true", Duration.ofMinutes(10));

            log.info("비밀번호 재설정 코드 인증 성공: {}", email);
            return true;
        }

        log.warn("비밀번호 재설정 코드 불일치: {}", email);
        return false;
    }

    /**
     * 이메일 인증 상태 확인 (회원가입 시 사용)
     */
    public boolean isEmailVerified(String email) {
        String verifiedKey = "email_verified:" + email;
        String verified = redisTemplate.opsForValue().get(verifiedKey);
        return "true".equals(verified);
    }

    /**
     * 비밀번호 재설정 권한 확인
     */
    public boolean canResetPassword(String email) {
        String resetTokenKey = "password_reset_verified:" + email;
        String verified = redisTemplate.opsForValue().get(resetTokenKey);
        return "true".equals(verified);
    }

    /**
     * 이메일 인증 상태 삭제 (회원가입 완료 후)
     */
    public void removeEmailVerificationStatus(String email) {
        String verifiedKey = "email_verified:" + email;
        redisTemplate.delete(verifiedKey);
    }

    /**
     * 비밀번호 재설정 권한 삭제 (비밀번호 변경 완료 후)
     */
    public void removePasswordResetPermission(String email) {
        String resetTokenKey = "password_reset_verified:" + email;
        redisTemplate.delete(resetTokenKey);
    }

    // === Private Methods ===

    /**
     * 6자리 랜덤 코드 생성
     */
    private String generateRandomCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    /**
     * 이메일 인증코드를 Redis에 저장
     */
    private void saveVerificationCode(String email, String code) {
        String redisKey = "email_verification:" + email;
        redisTemplate.opsForValue().set(redisKey, code, Duration.ofMinutes(CODE_EXPIRY_MINUTES));
    }

    /**
     * 비밀번호 재설정 코드를 Redis에 저장
     */
    private void savePasswordResetCode(String email, String code) {
        String redisKey = "password_reset:" + email;
        redisTemplate.opsForValue().set(redisKey, code, Duration.ofMinutes(CODE_EXPIRY_MINUTES));
    }

    /**
     * 회원가입용 인증 이메일 발송
     */
    private void sendVerificationEmail(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[OneToDay] 이메일 인증코드");
            helper.setText(createVerificationEmailContent(code), true);
            helper.setFrom("hwangsubin93@gmail.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("인증 이메일 발송 실패: {}", email, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }
    }

    /**
     * 비밀번호 재설정용 이메일 발송
     */
    private void sendPasswordResetEmail(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[OneToDay] 비밀번호 재설정 인증코드");
            helper.setText(createPasswordResetEmailContent(code), true);
            helper.setFrom("hwangsubin93@gmail.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("비밀번호 재설정 이메일 발송 실패: {}", email, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }
    }

    /**
     * 회원가입용 이메일 HTML 템플릿
     */
    private String createVerificationEmailContent(String code) {
        return """
            <div style="max-width: 600px; margin: 0 auto; padding: 20px; font-family: Arial, sans-serif;">
                <div style="text-align: center; margin-bottom: 30px;">
                    <h1 style="color: #10b981;">OneToDay</h1>
                    <h2 style="color: #374151;">이메일 인증</h2>
                </div>
                
                <div style="background-color: #f9fafb; padding: 30px; border-radius: 8px; text-align: center;">
                    <p style="font-size: 16px; color: #6b7280; margin-bottom: 20px;">
                        회원가입을 완료하기 위해 아래 인증코드를 입력해주세요.
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

    /**
     * 비밀번호 재설정용 이메일 HTML 템플릿
     */
    private String createPasswordResetEmailContent(String code) {
        return """
            <div style="max-width: 600px; margin: 0 auto; padding: 20px; font-family: Arial, sans-serif;">
                <div style="text-align: center; margin-bottom: 30px;">
                    <h1 style="color: #ef4444;">OneToDay</h1>
                    <h2 style="color: #374151;">비밀번호 재설정</h2>
                </div>
                
                <div style="background-color: #fef2f2; padding: 30px; border-radius: 8px; text-align: center; border: 1px solid #fecaca;">
                    <p style="font-size: 16px; color: #6b7280; margin-bottom: 20px;">
                        비밀번호 재설정을 위해 아래 인증코드를 입력해주세요.
                    </p>
                    
                    <div style="background-color: #ef4444; color: white; font-size: 32px; font-weight: bold; 
                                padding: 20px; border-radius: 8px; letter-spacing: 8px; margin: 20px 0;">
                        %s
                    </div>
                    
                    <p style="font-size: 14px; color: #9ca3af;">
                        인증코드는 5분간 유효하며, 인증 후 10분간 비밀번호를 변경할 수 있습니다.
                    </p>
                    
                    <div style="background-color: #fef3c7; padding: 15px; border-radius: 6px; margin-top: 20px; border: 1px solid #fbbf24;">
                        <p style="font-size: 13px; color: #92400e; margin: 0;">
                            ⚠️ 본인이 요청하지 않은 경우, 즉시 고객센터로 연락해주세요.
                        </p>
                    </div>
                </div>
                
                <div style="text-align: center; margin-top: 30px;">
                    <p style="font-size: 12px; color: #9ca3af;">
                        본 이메일은 발신전용입니다. 보안을 위해 타인과 인증코드를 공유하지 마세요.
                    </p>
                </div>
            </div>
            """.formatted(code);
    }
}


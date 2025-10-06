package com.otd.otd_user.application.email;

import com.otd.otd_user.application.email.model.MunheEmailReq;
import com.otd.otd_user.application.user.UserRepository;
import com.otd.otd_user.entity.EmailVerification;
import com.otd.otd_user.entity.Munhe;
import com.otd.otd_user.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;
    private final MunheRepository munheRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int CODE_EXPIRY_MINUTES = 5;
    private static final int VERIFICATION_STATUS_EXPIRY_MINUTES = 30;
    private static final int PASSWORD_RESET_EXPIRY_MINUTES = 10;
    private final JavaMailSender javaMailSender;

    /**
     * 회원가입용 이메일 인증코드 발송
     */
    @Transactional
    public void sendEmailVerificationCode(String email) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 2. 기존 인증 정보 삭제
        emailVerificationRepository.deleteByEmailAndType(email, EmailVerification.VerificationType.SIGNUP);

        // 3. 인증코드 생성 및 저장
        String verificationCode = generateRandomCode();
        EmailVerification verification = new EmailVerification();
        verification.setEmail(email);
        verification.setCode(verificationCode);
        verification.setType(EmailVerification.VerificationType.SIGNUP);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));

        emailVerificationRepository.save(verification);

        // 4. 이메일 발송
        sendVerificationEmail(email, verificationCode);

        log.info("회원가입용 이메일 인증코드 발송 완료: {}", email);
    }

    /**
     * 비밀번호 재설정용 이메일 인증코드 발송
     */
    @Transactional
    public void sendPasswordResetCode(String email) {
        // 1. 이메일 존재 여부 체크
        if (!userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("등록되지 않은 이메일입니다.");
        }

        // 2. 기존 인증 정보 삭제
        emailVerificationRepository.deleteByEmailAndType(email, EmailVerification.VerificationType.PASSWORD_RESET);

        // 3. 인증코드 생성 및 저장
        String resetCode = generateRandomCode();
        EmailVerification verification = new EmailVerification();
        verification.setEmail(email);
        verification.setCode(resetCode);
        verification.setType(EmailVerification.VerificationType.PASSWORD_RESET);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));

        emailVerificationRepository.save(verification);

        // 4. 이메일 발송
        sendPasswordResetEmail(email, resetCode);

        log.info("비밀번호 재설정용 이메일 인증코드 발송 완료: {}", email);
    }

    /**
     * 이메일 인증코드 검증 (회원가입용)
     */
    @Transactional
    public boolean verifyEmailCode(String email, String code) {
        Optional<EmailVerification> verificationOpt = emailVerificationRepository
                .findTopByEmailAndTypeOrderByCreatedAtDesc(email, EmailVerification.VerificationType.SIGNUP);

        if (verificationOpt.isEmpty()) {
            log.warn("인증코드가 존재하지 않습니다: {}", email);
            return false;
        }

        EmailVerification verification = verificationOpt.get();

        if (!verification.canVerify()) {
            log.warn("인증코드가 만료되었거나 이미 인증되었습니다: {}", email);
            return false;
        }

        if (!verification.getCode().equals(code)) {
            log.warn("인증코드 불일치: {}", email);
            return false;
        }

        // 인증 성공 처리
        verification.setVerified(true);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(VERIFICATION_STATUS_EXPIRY_MINUTES));
        emailVerificationRepository.save(verification);

        log.info("이메일 인증 성공: {}", email);
        return true;
    }

    /**
     * 비밀번호 재설정 코드 검증
     */
    @Transactional
    public boolean verifyPasswordResetCode(String email, String code) {
        Optional<EmailVerification> verificationOpt = emailVerificationRepository
                .findTopByEmailAndTypeOrderByCreatedAtDesc(email, EmailVerification.VerificationType.PASSWORD_RESET);

        if (verificationOpt.isEmpty()) {
            log.warn("비밀번호 재설정 코드가 존재하지 않습니다: {}", email);
            return false;
        }

        EmailVerification verification = verificationOpt.get();

        if (!verification.canVerify()) {
            log.warn("비밀번호 재설정 코드가 만료되었거나 이미 인증되었습니다: {}", email);
            return false;
        }

        if (!verification.getCode().equals(code)) {
            log.warn("비밀번호 재설정 코드 불일치: {}", email);
            return false;
        }

        // 인증 성공 처리 (비밀번호 재설정 권한 부여)
        verification.setVerified(true);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(PASSWORD_RESET_EXPIRY_MINUTES));
        emailVerificationRepository.save(verification);

        log.info("비밀번호 재설정 코드 인증 성공: {}", email);
        return true;
    }

    /**
     * 이메일 인증 상태 확인 (회원가입 시 사용)
     */
    public boolean isEmailVerified(String email) {
        Optional<EmailVerification> verificationOpt = emailVerificationRepository
                .findVerifiedByEmailAndType(email, EmailVerification.VerificationType.SIGNUP, LocalDateTime.now());

        return verificationOpt.isPresent();
    }

    /**
     * 비밀번호 재설정 권한 확인
     */
    public boolean canResetPassword(String email) {
        Optional<EmailVerification> verificationOpt = emailVerificationRepository
                .findVerifiedByEmailAndType(email, EmailVerification.VerificationType.PASSWORD_RESET, LocalDateTime.now());

        return verificationOpt.isPresent();
    }

    /**
     * 이메일 인증 상태 삭제 (회원가입 완료 후)
     */
    @Transactional
    public void removeEmailVerificationStatus(String email) {
        emailVerificationRepository.deleteByEmailAndType(email, EmailVerification.VerificationType.SIGNUP);
    }

    /**
     * 비밀번호 재설정 권한 삭제 (비밀번호 변경 완료 후)
     */
    @Transactional
    public void removePasswordResetPermission(String email) {
        emailVerificationRepository.deleteByEmailAndType(email, EmailVerification.VerificationType.PASSWORD_RESET);
    }

    /**
     * 만료된 인증 정보 정리 (스케줄러)
     */
    @Scheduled(cron = "0 0 * * * *") // 매시간 실행
    @Transactional
    public void cleanupExpiredVerifications() {
        int deletedCount = emailVerificationRepository.deleteExpiredVerifications(LocalDateTime.now());
        if (deletedCount > 0) {
            log.info("만료된 이메일 인증 정보 {} 건 삭제 완료", deletedCount);
        }
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
     * 이메일 변경용 인증 이메일 발송
     */
    private void sendEmailUpdateVerification(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[OneToDay] 이메일 변경 인증코드");
            helper.setText(createEmailUpdateVerificationContent(code), true);
            helper.setFrom("hwangsubin93@gmail.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("이메일 변경 인증 이메일 발송 실패: {}", email, e);
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
     * 이메일 변경용 이메일 HTML 템플릿
     */
    private String createEmailUpdateVerificationContent(String code) {
        return """
            <div style="max-width: 600px; margin: 0 auto; padding: 20px; font-family: Arial, sans-serif;">
                <div style="text-align: center; margin-bottom: 30px;">
                    <h1 style="color: #3b82f6;">OneToDay</h1>
                    <h2 style="color: #374151;">이메일 변경 인증</h2>
                </div>
            
                <div style="background-color: #eff6ff; padding: 30px; border-radius: 8px; text-align: center; border: 1px solid #bfdbfe;">
                    <p style="font-size: 16px; color: #6b7280; margin-bottom: 20px;">
                        이메일 변경을 완료하기 위해 아래 인증코드를 입력해주세요.
                    </p>
            
                    <div style="background-color: #3b82f6; color: white; font-size: 32px; font-weight: bold; 
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


    /**
     * 문의하기 이메일 전송
     */
    public void sendInquiryEmail(MunheEmailReq req) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 받는 이메일 주소 (관리자 이메일)
            helper.setTo("hwangsubin93@gmail.com");
            helper.setSubject("[웹사이트 문의] " + req.getSubject());

            // HTML 형태의 이메일 내용 구성
            String htmlContent = buildInquiryEmailContent(req);
            helper.setText(htmlContent, true);

            // 보낸이 정보 설정
            helper.setFrom("hwangsubin93@gmail.com", "OTD 웹사이트");

            javaMailSender.send(message);
            log.info("문의하기 이메일 전송 성공: {}", req.getSubject());

        } catch (Exception e) {
            log.error("문의하기 이메일 전송 실패", e);
            throw new RuntimeException("이메일 전송에 실패했습니다.", e);
        }
    }

    /**
     * 문의 이메일 내용 구성
     */
    private String buildInquiryEmailContent(MunheEmailReq req) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<div style='max-width: 600px; margin: 0 auto; font-family: Arial, sans-serif;'>");
        html.append("<h2 style='color: #333; border-bottom: 2px solid #007bff; padding-bottom: 10px;'>");
        html.append("새로운 문의가 접수되었습니다");
        html.append("</h2>");

        html.append("<div style='background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin: 20px 0;'>");
        html.append("<p><strong>제목:</strong> ").append(escapeHtml(req.getSubject())).append("</p>");
        html.append("<p><strong>보낸이:</strong> ").append(escapeHtml(req.getSenderName())).append("</p>");

        if (req.getSenderEmail() != null && !req.getSenderEmail().isEmpty()) {
            html.append("<p><strong>이메일:</strong> ").append(escapeHtml(req.getSenderEmail())).append("</p>");
        }

        html.append("<p><strong>접수시간:</strong> ");
        if (req.getTimestamp() != null) {
            html.append(req.getTimestamp());
        } else {
            html.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        html.append("</p>");
        html.append("</div>");

        html.append("<div style='background-color: #ffffff; padding: 20px; border: 1px solid #dee2e6; border-radius: 5px;'>");
        html.append("<h4 style='color: #495057; margin-top: 0;'>문의 내용:</h4>");
        html.append("<p style='line-height: 1.6; white-space: pre-wrap;'>");
        html.append(escapeHtml(req.getMessage()));
        html.append("</p>");
        html.append("</div>");

        html.append("<div style='text-align: center; margin-top: 20px; padding: 10px; background-color: #e9ecef; border-radius: 5px;'>");
        html.append("<p style='color: #6c757d; font-size: 12px; margin: 0;'>");
        html.append("이 메일은 OTD 웹사이트 문의하기를 통해 자동으로 발송되었습니다.");
        html.append("</p>");
        html.append("</div>");

        html.append("</div>");
        html.append("</body></html>");

        return html.toString();
    }

    /**
     * HTML 이스케이프 처리
     */
    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    @Transactional
    public void sendMunheEmail(MunheEmailReq req, Long userId) {
        // DB에 저장
        Munhe munhe = new Munhe();
        munhe.setUserId(userId);
        munhe.setSubject(req.getSubject());
        munhe.setContent(req.getMessage());
        // userId로 User 테이블에서 이메일 조회
        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
            munhe.setSenderEmail(user.getEmail());
            munhe.setSenderName(user.getNickName());
        } else {
            munhe.setSenderEmail(req.getSenderEmail());
            munhe.setSenderName(req.getSenderName());
        }
        Munhe savedMunhe = munheRepository.save(munhe);
        log.info("문의 DB 저장 완료. ID: {}, 사용자: {}", savedMunhe.getId(), userId);

        // 이메일 전송
        sendInquiryEmail(req);
        log.info("문의 이메일 전송 완료");
    }
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
    @Transactional
    public void updateEmail(Long userId, String newEmail) {
        if (!isEmailAvailable(newEmail)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        user.setEmail(newEmail);
        userRepository.save(user);
    }
    /**
     * 이메일 변경용 인증코드 발송
     */
    @Transactional
    public void sendEmailUpdateCode(String newEmail, Long userId) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 2. 기존 인증 정보 삭제
        emailVerificationRepository.deleteByEmailAndType(newEmail, EmailVerification.VerificationType.EMAIL_UPDATE);

        // 3. 인증코드 생성 및 저장
        String verificationCode = generateRandomCode();
        EmailVerification verification = new EmailVerification();
        verification.setEmail(newEmail);
        verification.setCode(verificationCode);
        verification.setType(EmailVerification.VerificationType.EMAIL_UPDATE);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));

        emailVerificationRepository.save(verification);

        // 4. 이메일 발송
        sendEmailUpdateVerification(newEmail, verificationCode);

        log.info("이메일 변경 인증코드 발송 완료: {} (사용자ID: {})", newEmail, userId);
    }

    /**
     * 이메일 변경 코드 검증
     */
    @Transactional
    public boolean verifyEmailUpdateCode(String email, String code) {
        Optional<EmailVerification> verificationOpt = emailVerificationRepository
                .findTopByEmailAndTypeOrderByCreatedAtDesc(email, EmailVerification.VerificationType.EMAIL_UPDATE);

        if (verificationOpt.isEmpty()) {
            log.warn("이메일 변경 인증코드가 존재하지 않습니다: {}", email);
            return false;
        }

        EmailVerification verification = verificationOpt.get();

        if (!verification.canVerify()) {
            log.warn("이메일 변경 인증코드가 만료되었거나 이미 인증되었습니다: {}", email);
            return false;
        }

        if (!verification.getCode().equals(code)) {
            log.warn("이메일 변경 인증코드 불일치: {}", email);
            return false;
        }

        // 인증 성공 처리
        verification.setVerified(true);
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(10)); // 10분간 변경 권한
        emailVerificationRepository.save(verification);

        log.info("이메일 변경 인증 성공: {}", email);
        return true;
    }

    /**
     * 이메일 변경 권한 확인
     */
    public boolean canUpdateEmail(String email) {
        Optional<EmailVerification> verificationOpt = emailVerificationRepository
                .findVerifiedByEmailAndType(email, EmailVerification.VerificationType.EMAIL_UPDATE, LocalDateTime.now());
        return verificationOpt.isPresent();
    }

    /**
     * 비밀번호 재설정 (이메일 인증 완료 후)
     */
    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        user.setUpw(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 비밀번호 재설정 권한 삭제
        removePasswordResetPermission(email);

        log.info("비밀번호 재설정 완료: {}", email);
    }

}
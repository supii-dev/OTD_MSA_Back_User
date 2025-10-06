package com.otd.otd_user.application.email;

import com.otd.configuration.model.UserPrincipal;
import com.otd.otd_user.application.email.model.*;
import com.otd.configuration.model.ResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/OTD/email")
@RequiredArgsConstructor public class EmailController {
    private final EmailService emailService;

    /**
     * 회원가입용 이메일 인증코드 발송
     */
    @PostMapping("/send-verification")
    public ResultResponse<?> sendEmailVerification(@Valid @RequestBody EmailSendReq req) {
        log.info("회원가입용 이메일 인증코드 발송 요청: {}", req.getEmail());
        emailService.sendEmailVerificationCode(req.getEmail());
        return new ResultResponse<>("인증코드가 발송되었습니다.", null);
    }

    /**
     * 회원가입용 이메일 인증코드 검증
     */
    @PostMapping("/verify-code")
    public ResultResponse<?> verifyEmailCode(@Valid @RequestBody EmailVerifyReq req) {
        log.info("회원가입용 이메일 인증코드 검증 요청: {}", req.getEmail());
        boolean isValid = emailService.verifyEmailCode(req.getEmail(), req.getCode());
        if (isValid) {
            return new ResultResponse<>("이메일 인증이 완료되었습니다.", Map.of("verified", true));
        } else {
            return new ResultResponse<>("인증코드가 일치하지 않습니다.", Map.of("verified", false));
        }
    }

    /**
     * 비밀번호 재설정용 이메일 인증코드 발송
     */
    @PostMapping("/send-password-reset")
    public ResultResponse<?> sendPasswordResetCode(@Valid @RequestBody EmailSendReq req) {
        log.info("비밀번호 재설정용 이메일 인증코드 발송 요청: {}", req.getEmail());
        emailService.sendPasswordResetCode(req.getEmail());
        return new ResultResponse<>("비밀번호 재설정 인증코드가 발송되었습니다.", null);
    }

    /**
     * 비밀번호 재설정용 인증코드 검증
     */
    @PostMapping("/verify-password-reset-code")
    public ResultResponse<?> verifyPasswordResetCode(@Valid @RequestBody EmailCodeVerifyReq req) {
        log.info("비밀번호 재설정용 인증코드 검증 요청: {}", req.getEmail());
        boolean isValid = emailService.verifyPasswordResetCode(req.getEmail(), req.getCode());
        if (isValid) {
            return new ResultResponse<>("인증이 완료되었습니다. 10분 내에 비밀번호를 변경해주세요.", Map.of("verified", true));
        } else {
            return new ResultResponse<>("인증코드가 일치하지 않습니다.", Map.of("verified", false));
        }
    }

    /**
     * 이메일 인증 상태 확인
     */
    @GetMapping("/verification-status/{email}")
    public ResultResponse<?> getEmailVerificationStatus(@PathVariable String email) {
        boolean isVerified = emailService.isEmailVerified(email);
        return new ResultResponse<>("이메일 인증 상태 조회", Map.of("email", email, "verified", isVerified));
    }

    /**
     * 문의하기 이메일 전송
     */
    @PostMapping("/sendMunhe")
    public ResultResponse<?> sendInquiryEmail(@Valid @RequestBody MunheEmailReq req, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getSignedUserId();
        log.info("문의하기 처리 시작: 제목={}, 보낸이={}, 사용자ID={}", req.getSubject(), req.getSenderName(), userId);
        try {
            emailService.sendMunheEmail(req, userId);
            return new ResultResponse<>("문의가 성공적으로 전송되었습니다.", Map.of("success", true, "timestamp", System.currentTimeMillis()));
        } catch (Exception e) {
            log.error("문의하기 이메일 전송 실패", e);
            return new ResultResponse<>("문의 전송에 실패했습니다. 잠시 후 다시 시도해주세요.", Map.of("success", false));
        }
    }

    @PatchMapping("/email-update")
    public ResponseEntity<?> updateEmail(@Valid @RequestBody EmailUpdateDto request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        // 이미 사용중인 이메일인지 확인
        if (!emailService.isEmailAvailable(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 사용중인 이메일입니다."));
        }
        emailService.updateEmail(userPrincipal.getSignedUserId(), request.getEmail());
        return ResponseEntity.ok(Map.of("success", true, "message", "이메일이 변경되었습니다.", "data", Map.of("email", request.getEmail())));
    }
}


package com.otd.otd_user.application.user;

import com.otd.otd_user.application.email.model.PasswordChangeReq;
import com.otd.otd_user.application.email.model.PasswordResetReq;
import com.otd.otd_user.application.user.model.*;
import com.otd.configuration.jwt.JwtTokenManager;
import com.otd.configuration.model.ResultResponse;
import com.otd.configuration.model.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/OTD/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenManager jwtTokenManager;
    private final PointService pointService;

    @PostMapping(
            value = "/join",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE // ← multipart만 받겠다고 명시
    )
    public ResultResponse<?> join( @Valid @RequestPart("req") UserJoinReq req,                  // ← 파트 이름 명시
                                   @RequestPart(value = "pic", required = false) MultipartFile pic) {
        log.info("pic: {}", pic != null ? pic.getOriginalFilename() : pic);
        userService.join(req, pic);
        return new ResultResponse<>("", 1);
    }

    @PostMapping("/login")
    public ResultResponse<?> login(@Valid @RequestBody UserLoginReq req, HttpServletResponse response) {
        log.info("req: {}", req);
        UserLoginDto userloginDto = userService.login(req);
        String refreshToken = jwtTokenManager.issue(response, userloginDto.getJwtUser());

        userService.updateRefreshToken(userloginDto.getUserLoginRes().getUserId(), refreshToken);
        return new ResultResponse<>("로그인 성공", userloginDto.getUserLoginRes());
    }

    //이메일 인증후 비밀번호 변경
    @PostMapping("/reset-password")
    public ResultResponse<?> resetPassword(@Valid @RequestBody PasswordResetReq req) {
        log.info("비밀번호 재설정 요청: {}", req.getEmail());
        userService.resetPassword(req);
        return new ResultResponse<>("비밀번호가 성공적으로 변경되었습니다.", null);
    }
    // 비밀번로 변경
    @PatchMapping("/password")
    public ResultResponse<?> changePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody PasswordChangeReq req) {
        userService.changePassword(userPrincipal.getSignedUserId(), req);
        return new ResultResponse<>("비밀번호가 성공적으로 변경되었습니다.", null);
    }

    // 아이디 중복 확인
    @GetMapping("/check-uid/{uid}")
    public ResultResponse<?> checkUidDuplicate(@PathVariable String uid) {
        boolean isAvailable = userService.isUidAvailable(uid);
        return new ResultResponse<>("아이디 중복 확인", Map.of("isAvailable", isAvailable));
    }

    // 닉네임 중복 확인
    @GetMapping("/check-nickname/{nickname}")
    public ResultResponse<?> checkNicknameDuplicate(@PathVariable String nickname) {
        boolean isAvailable = userService.isNicknameAvailable(nickname);
        return new ResultResponse<>("닉네임 중복 확인", Map.of("isAvailable", isAvailable));
    }

    @PostMapping("/logout")
    public ResultResponse<?> logout(HttpServletResponse response) {
        log.info("logout");
        jwtTokenManager.logout(response);

        return new ResultResponse<>("sign-out 성공", null);
    }


    @PostMapping("/reissue")
    public ResultResponse<?> reissue(HttpServletResponse response, HttpServletRequest request) {
        log.info("reissue request: {}  respone: {}", request,  response);
        jwtTokenManager.reissue(request, response);
        return new ResultResponse<>("AccessToken 재발행 성공", null);
    }

    @GetMapping("/profile")
    public ResultResponse<?> getProfileUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserProfileGetRes userProfileGetRes = userService.getProfileUser(userPrincipal.getSignedUserId());
        return new ResultResponse<>("프로파일 유저 정보", userProfileGetRes);
    }

    @PatchMapping("/profile/pic")
    public ResultResponse<?> patchProfilePic(@AuthenticationPrincipal UserPrincipal userPrincipal
            , @RequestPart MultipartFile pic) {
        String savedFileName = userService.patchProfilePic(userPrincipal.getSignedUserId(), pic);
        return new ResultResponse<>("프로파일 사진 수정 완료", savedFileName);
    }

    @DeleteMapping("/profile/pic")
    public ResultResponse<?> deleteProfilePic(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        userService.deleteProfilePic(userPrincipal.getSignedUserId());
        return new ResultResponse<>("프로파일 사진 삭제 완료", null);
    }
    @GetMapping("/pointhistory/{userId}")
    public ResultResponse<?> getPointHistory(@PathVariable Long userId) {
        PointHistoryResponseDTO response = pointService.getPointHistory(userId);
        return new ResultResponse<>("포인트 내역 조회 성공", response);
    }
}
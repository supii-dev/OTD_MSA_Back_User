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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/OTD/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenManager jwtTokenManager;

    @PostMapping("/join")
    public ResultResponse<?> join(@Valid @RequestPart UserJoinReq req
            , @RequestPart(required = false) MultipartFile pic) {
        log.info("req: {}", req);
        log.info("pic: {}", pic != null ? pic.getOriginalFilename() : pic);
        userService.join(req, pic);
        return new ResultResponse<>("회원가입이 완료되었습니다.", 1);
    }

    @PostMapping("/login")
    public ResultResponse<?> login(@Valid @RequestBody UserLoginReq req, HttpServletResponse response) {
        log.info("req: {}", req);
        UserLoginDto userloginDto = userService.login(req);
        jwtTokenManager.issue(response, userloginDto.getJwtUser());
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

    // 중복가입 확인
    @PostMapping("/check-duplicate")
    public ResultResponse<?> checkDuplicateUser(@RequestBody Map<String, String> req) {
        String ci = req.get("ci");
        String di = req.get("di");
        boolean isDuplicate = userService.isDuplicateUser(ci, di);
        return new ResultResponse<>("중복가입 확인", Map.of("isDuplicate", isDuplicate));
    }

    @PostMapping("/logout")
    public ResultResponse<?> logout(HttpServletResponse response) {
        jwtTokenManager.logout(response);
        return new ResultResponse<>("sign-out 성공", null);
    }

    @PostMapping("/test")
    public String test() {
        return "테스트 성공";
    }

    @PostMapping("/reissue")
    public ResultResponse<?> reissue(HttpServletResponse response, HttpServletRequest request) {
        jwtTokenManager.reissue(request, response);
        return new ResultResponse<>("AccessToken 재발행 성공", null);
    }

    @GetMapping("/profile")
    public ResultResponse<?> getProfileUser(@AuthenticationPrincipal UserPrincipal userPrincipal
            , @RequestParam("profile_user_id") long profileUserId) {
        log.info("profileUserId: {}", profileUserId);
        UserProfileGetDto dto = new UserProfileGetDto(userPrincipal.getSignedUserId(), profileUserId);
        UserProfileGetRes userProfileGetRes = userService.getProfileUser(dto);
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
}
package com.otd.configuration.security.oauth;


import com.otd.configuration.constants.ConstJwt;
import com.otd.configuration.constants.ConstOAuth2;
import com.otd.configuration.jwt.JwtTokenManager;
import com.otd.configuration.model.JwtUser;
import com.otd.configuration.model.UserPrincipal;
import com.otd.configuration.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;  // ✅ 이걸로 변경!
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class Oauth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final Oauth2AuthenticationRequestBasedOnCookieRepository repository;
    private final JwtTokenManager jwtTokenManager;
    private final ConstOAuth2 constOAuth2;
    private final CookieUtils cookieUtils;
    private final ConstJwt constJwt;

    @Value("${spring.jwt.register-token.expiration-time:1800000}") // 30분
    private long REGISTER_TOKEN_EXPIRATION_TIME;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth)
            throws IOException, ServletException {
        if(res.isCommitted()) {
            log.error("onAuthenticationSuccess called with a committed response {}", res);
            return;
        }

        String targetUrl = this.determineTargetUrl(req, res, auth);
        log.info("onAuthenticationSuccess targetUrl={}", targetUrl);
        clearAuthenticationAttributes(req, res);
        getRedirectStrategy().sendRedirect(req, res, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest req, HttpServletResponse res, Authentication auth) {
        String redirectUrl = cookieUtils.getValue(req, constOAuth2.redirectUriParamCookieName, String.class);
        String targetUrl = redirectUrl == null ? getDefaultTargetUrl() : redirectUrl;

        UserPrincipal myUserDetails = (UserPrincipal) auth.getPrincipal();
        OAuth2JwtUser oauth2JwtUser = (OAuth2JwtUser)myUserDetails.getJwtUser();

        // userId가 0이면 신규 유저 (온보딩 필요)
        if (oauth2JwtUser.getSignedUserId() == 0) {
            log.info("신규 소셜 유저 - 레지스터 토큰 발급");

            // 레지스터 토큰 생성 (소셜 정보 포함)
            String registerToken = jwtTokenManager.generateSocialRegisterToken(
                    oauth2JwtUser.getNickName(),
                    oauth2JwtUser.getPic(),
                    oauth2JwtUser.getProviderType(),  // 추가
                    oauth2JwtUser.getProviderId(),    // 추가
                    REGISTER_TOKEN_EXPIRATION_TIME
            );

            // 쿠키에 저장
            cookieUtils.setCookie(res, "registerToken", registerToken,
                    (int) REGISTER_TOKEN_EXPIRATION_TIME / 1000, "/", constJwt.getDomain());
            cookieUtils.setCookie(res, "needsOnboarding", "true",
                    (int) REGISTER_TOKEN_EXPIRATION_TIME / 1000, "/", constJwt.getDomain());

            return targetUrl + "?needs_onboarding=true";
        }

        // 기존 유저 - 정상 토큰 발급
        log.info("기존 소셜 유저 - 정상 로그인");
        JwtUser jwtUser = new JwtUser(oauth2JwtUser.getSignedUserId(), oauth2JwtUser.getRoles());
        jwtTokenManager.issue(res, jwtUser);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("user_id", oauth2JwtUser.getSignedUserId())
                .queryParam("nick_name", oauth2JwtUser.getNickName()).encode()
                .queryParam("pic", oauth2JwtUser.getPic())
                .queryParam("name", oauth2JwtUser.getName())
                .build()
                .toUriString();
    }

    private void clearAuthenticationAttributes(HttpServletRequest req, HttpServletResponse res) {
        super.clearAuthenticationAttributes(req);
    }
}


package com.otd.configuration.security.oauth;

import com.otd.configuration.enumcode.model.EnumChallengeRole;
import com.otd.otd_user.application.user.UserRepository;
import com.otd.configuration.enumcode.model.EnumUserRole;
import com.otd.configuration.model.JwtUser;
import com.otd.configuration.model.UserPrincipal;
import com.otd.configuration.security.SignInProviderType;
import com.otd.configuration.security.oauth.userinfo.Oauth2UserInfo;
import com.otd.configuration.security.oauth.userinfo.Oauth2UserInfoFactory;
import com.otd.otd_user.entity.User;
import com.otd.otd_user.entity.UserRole;
import com.otd.otd_user.entity.UserRoleIds;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class MyOauth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final Oauth2UserInfoFactory oauth2UserInfoFactory;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) {
        try {
            return process(req);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest req) {
        OAuth2User oAuth2User = super.loadUser(req);

        SignInProviderType signInProviderType = SignInProviderType.valueOf(
                req.getClientRegistration().getRegistrationId().toUpperCase()
        );

        Oauth2UserInfo oauth2UserInfo = oauth2UserInfoFactory.getOauth2UserInfo(
                signInProviderType,
                oAuth2User.getAttributes()
        );

        // providerId 생성 (예: KAKAO_1234567890)
        String providerId = signInProviderType.name() + "_" + oauth2UserInfo.getId();

        // providerId로 기존 유저 확인
        User user = userRepository.findByProviderId(providerId);

        if(user == null) {
            // 신규 유저 - DB에 저장하지 않고 임시 정보만 전달
            log.info("신규 소셜 유저 - 온보딩 필요: {}", providerId);

            // 임시 JwtUser 생성 (온보딩용)
            List<EnumUserRole> tempRoles = List.of(EnumUserRole.USER_2);
            JwtUser jwtUser = new OAuth2JwtUser(
                    null, // name - 신규 유저는 이름 없음
                    oauth2UserInfo.getName(), // nickName
                    oauth2UserInfo.getProfileImageUrl(), // pic
                    0L, // userId는 0 (아직 생성 안됨)
                    tempRoles,
                    signInProviderType.name(), // providerType 추가
                    providerId // providerId 추가
            );

            UserPrincipal myUserDetails = new UserPrincipal(jwtUser);
            return myUserDetails;
        }

        // 기존 유저 - 정상 로그인 처리
        log.info("기존 소셜 유저 로그인: {}", user.getUserId());

        List<EnumUserRole> roles = user.getUserRoles().stream()
                .map(item -> item.getUserRoleIds().getRoleCode())
                .toList();

        String nickName = user.getNickName() == null ? user.getUid() : user.getNickName();
        JwtUser jwtUser = new OAuth2JwtUser(
                user.getName(), // name - 기존 유저의 이름 (User 엔티티에 name 필드가 있어야 함)
                nickName, // nickName
                user.getPic(), // pic
                user.getUserId(),
                roles,
                signInProviderType.name(),
                providerId
        );

        UserPrincipal myUserDetails = new UserPrincipal(jwtUser);
        return myUserDetails;
    }
}
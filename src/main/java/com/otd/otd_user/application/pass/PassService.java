package com.otd.otd_user.application.pass;

import com.otd.otd_user.application.pass.config.PassClientIds;
import com.otd.otd_user.application.pass.config.PassClientSecrets;
import com.otd.otd_user.application.pass.model.PassAuthResult;
import com.otd.otd_user.application.pass.model.PassAuthVerifyReq;
import com.otd.otd_user.application.pass.model.PassUserInfo;
import com.otd.otd_user.application.user.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PassService {
    private final UserMapper userMapper;
    private final PassClientIds passClientIds;
    private final PassClientSecrets passClientSecrets;
    private final RestTemplate restTemplate;

    private boolean isDuplicateUser(String ci, String di) {
        int count = 0;
        if (ci != null && !ci.isEmpty()) {
            count += userMapper.countByCi(ci);
        }
        if (di != null && !di.isEmpty()) {
            count += userMapper.countByDi(di);
        }
        return count > 0;
    }

    public PassAuthResult verifyPassAuth(PassAuthVerifyReq req) {
        try {
            log.info("PASS 인증 결과 검증 시작: provider={}, authCode={}", req.getProvider(), req.getAuthCode());

            // 1. 액세스 토큰 요청
            String accessToken = getAccessTokenFromPass(req);

            // 2. 사용자 정보 조회
            PassUserInfo userInfo = getUserInfoFromPass(accessToken, req.getProvider());

            // 3. 중복 가입 확인
            if (isDuplicateUser(userInfo.getCi(), userInfo.getDi())) {
                throw new IllegalArgumentException("이미 가입된 사용자입니다.");
            }

            // 4. 응답 데이터 생성
            return PassAuthResult.builder()
                    .success(true)
                    .name(userInfo.getName())
                    .phone(formatPhoneNumber(userInfo.getPhone()))
                    .birthDate(userInfo.getBirthDate())
                    .gender(userInfo.getGender())
                    .ci(userInfo.getCi())
                    .di(userInfo.getDi())
                    .authToken(generateAuthToken(userInfo))
                    .provider(req.getProvider())
                    .build();

        } catch (Exception e) {
            log.error("PASS 인증 검증 실패: {}", e.getMessage(), e);
            return PassAuthResult.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    // PASS에서 액세스 토큰 가져오기
    private String getAccessTokenFromPass(PassAuthVerifyReq req) {
        try {
            // 통신사별 토큰 URL
            String tokenUrl = getTokenUrl(req.getProvider());

            // 토큰 요청 데이터
            Map<String, String> tokenRequest = Map.of(
                    "grant_type", "authorization_code",
                    "client_id", getClientId(req.getProvider()),
                    "client_secret", getClientSecret(req.getProvider()),
                    "code", req.getAuthCode(),
                    "redirect_uri", req.getRedirectUri()
            );

            // HTTP 요청
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            tokenRequest.forEach(body::add);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("access_token");
            } else {
                throw new RuntimeException("액세스 토큰 요청 실패");
            }

        } catch (Exception e) {
            log.error("액세스 토큰 요청 실패: {}", e.getMessage(), e);
            throw new RuntimeException("PASS 인증 토큰 요청에 실패했습니다.", e);
        }
    }

    // PASS에서 사용자 정보 가져오기
    private PassUserInfo getUserInfoFromPass(String accessToken, String provider) {
        try {
            String userInfoUrl = getUserInfoUrl(provider);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = this.restTemplate.exchange(
                    userInfoUrl, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return mapToPassUserInfo(response.getBody(), provider);
            } else {
                throw new RuntimeException("사용자 정보 조회 실패");
            }

        } catch (Exception e) {
            log.error("사용자 정보 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("PASS 사용자 정보 조회에 실패했습니다.", e);
        }
    }

    // 통신사별 토큰 URL
    private String getTokenUrl(String provider) {
        return switch (provider.toUpperCase()) {
            case "SKT" -> "https://tauth.telecom.co.kr/oauth/oauth_token.omp";
            case "KT" -> "https://auth.kt.com/oauth/oauth_token.omp";
            case "LGU" -> "https://uauth.lguplus.co.kr/oauth/oauth_token.omp";
            case "PAYCO" -> "https://id.payco.com/oauth2.0/token";
            case "SAMSUNG" -> "https://account.samsung.com/accounts/v1/oauth2/token";
            default -> throw new IllegalArgumentException("지원하지 않는 PASS 제공사: " + provider);
        };
    }

    // 통신사별 사용자 정보 URL
    private String getUserInfoUrl(String provider) {
        return switch (provider.toUpperCase()) {
            case "SKT" -> "https://tauth.telecom.co.kr/bas/oauth/user_info.omp";
            case "KT" -> "https://auth.kt.com/oauth/user_info.omp";
            case "LGU" -> "https://uauth.lguplus.co.kr/oauth/user_info.omp";
            case "PAYCO" -> "https://apis.payco.com/v1/user";
            case "SAMSUNG" -> "https://account.samsung.com/accounts/v1/user";
            default -> throw new IllegalArgumentException("지원하지 않는 PASS 제공사: " + provider);
        };
    }

    // 통신사별 클라이언트 ID (application.yaml에서 관리)
    private String getClientId(String provider) {
        return switch (provider.toUpperCase()) {
            case "SKT" -> passClientIds.getSkt();
            case "KT" -> passClientIds.getKt();
            case "LGU" -> passClientIds.getLgu();
            default -> throw new IllegalArgumentException("지원하지 않는 PASS 제공사: " + provider);
        };
    }

    // 통신사별 클라이언트 시크릿 (application.yaml에서 관리)
    private String getClientSecret(String provider) {
        return switch (provider.toUpperCase()) {
            case "SKT" -> passClientSecrets.getSkt();
            case "KT" -> passClientSecrets.getKt();
            case "LGU" -> passClientSecrets.getLgu();
            case "PAYCO" -> passClientSecrets.getPayco();
            case "SAMSUNG" -> passClientSecrets.getSamsung();
            default -> throw new IllegalArgumentException("지원하지 않는 PASS 제공사: " + provider);
        };
    }

    // 응답 데이터를 PassUserInfo로 매핑
    private PassUserInfo mapToPassUserInfo(Map<String, Object> responseData, String provider) {
        // 통신사별로 응답 구조가 다르므로 각각 처리
        return switch (provider.toUpperCase()) {
            case "SKT", "KT", "LGU" -> PassUserInfo.builder()
                    .name((String) responseData.get("name"))
                    .phone((String) responseData.get("phone"))
                    .birthDate((String) responseData.get("birth_date"))
                    .gender((String) responseData.get("gender"))
                    .ci((String) responseData.get("ci"))
                    .di((String) responseData.get("di"))
                    .build();
            case "PAYCO" -> {
                Map<String, Object> member = (Map<String, Object>) responseData.get("member");
                yield PassUserInfo.builder()
                        .name((String) member.get("name"))
                        .phone((String) member.get("mobile"))
                        .birthDate((String) member.get("birthday"))
                        .gender((String) member.get("gender"))
                        .ci((String) member.get("ci"))
                        .di((String) member.get("di"))
                        .build();
            }
            case "SAMSUNG" -> PassUserInfo.builder()
                    .name((String) responseData.get("user_name"))
                    .phone((String) responseData.get("mobile"))
                    .birthDate((String) responseData.get("birthday"))
                    .gender((String) responseData.get("gender"))
                    .ci((String) responseData.get("ci"))
                    .di((String) responseData.get("di"))
                    .build();
            default -> throw new IllegalArgumentException("지원하지 않는 PASS 제공사: " + provider);
        };
    }

    // 휴대폰 번호 포맷팅
    private String formatPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return "";
        }

        // 숫자만 추출
        String numbers = phone.replaceAll("[^0-9]", "");

        // 010-1234-5678 형식으로 변환
        if (numbers.length() == 11 && numbers.startsWith("010")) {
            return numbers.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
        }

        return phone;
    }

    // 인증 토큰 생성
    private String generateAuthToken(PassUserInfo userInfo) {
        String tokenData = userInfo.getCi() + "_" + userInfo.getDi() + "_" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(tokenData.getBytes());
    }

}

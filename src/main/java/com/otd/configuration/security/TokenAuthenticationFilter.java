package com.otd.configuration.security;

import com.otd.configuration.jwt.JwtTokenManager;
import com.otd.configuration.model.JwtUser;
import com.otd.configuration.model.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static org.springframework.web.util.WebUtils.getCookie;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenManager jwtTokenManager;

    //    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        log.info("request.getRequestURI(): {}", request.getRequestURI());
//        //토큰 처리
//        Authentication authentication = jwtTokenManager.getAuthentication(request);
//        if (authentication != null) {
//            SecurityContextHolder.getContext().setAuthentication(authentication); //인증 처리
//        }
//        filterChain.doFilter(request, response); //다음 필터에게 req, res 넘기기
//    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String uri = request.getRequestURI();
        log.info("요청 URI: {}", uri);
        // 인증 불필요 엔드포인트는 바로 통과
        if ("/api/OTD/user/join".equals(uri)
                || "/api/OTD/user/login".equals(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1) 재발급: refresh로만 검증. 실패 시 401로 즉시 종료 (중요!)
        if ("/api/OTD/user/reissue".equals(uri)) {
            String refresh = jwtTokenManager.getRefreshTokenFromCookie(request);
            if (refresh == null || refresh.isBlank()) {
                log.warn("리이슈 요청에 refresh 쿠키 없음");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                return;
            }
            try {
                JwtUser jwtUser = jwtTokenManager.getJwtUserFromToken(refresh); // 만료/위조면 예외
                UserPrincipal principal = new UserPrincipal(jwtUser);
                Authentication auth =
                        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("리이슈 인증 성공 (refresh)"); // 토큰 원문 로깅 금지
                filterChain.doFilter(request, response); // 컨트롤러에서 실제 reissue 수행
            } catch (Exception e) {
                log.warn("리이슈 인증 실패 (refresh invalid/expired): {}", e.getMessage());
                // (선택) 쿠키 정리: 만료/위조면 양쪽 다 지우고 클라이언트에 재로그인 유도
                // jwtTokenManager.deleteAccessTokenInCookie(response);
                // jwtTokenManager.deleteRefreshTokenInCookie(response);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            }
            return;
        }

        // 2) 그 외: access로만 인증 시도 (refresh 사용 금지)
        String access = jwtTokenManager.getAccessTokenFromCookie(request);
        if (access != null && !access.isBlank()) {
            try {
                JwtUser jwtUser = jwtTokenManager.getJwtUserFromToken(access);
                UserPrincipal principal = new UserPrincipal(jwtUser);
                Authentication auth =
                        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("액세스 인증 성공");
            } catch (Exception e) {
                log.debug("액세스 인증 실패 (invalid/expired): {}", e.getMessage());
                // 여기서는 체인 계속 진행 → SecurityConfig에서 authenticated()면 최종 401로 떨어짐
            }
        } else {
            log.trace("access 쿠키 없음");
        }

        filterChain.doFilter(request, response);
    }
}
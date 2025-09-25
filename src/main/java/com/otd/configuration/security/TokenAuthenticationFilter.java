package com.otd.configuration.security;

import com.otd.configuration.jwt.JwtTokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

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
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    log.info("요청 URI: {}", request.getRequestURI());

    // 로그아웃 요청은 토큰 없어도 통과
    if (request.getRequestURI().equals("/api/OTD/user/logout") ||
            request.getRequestURI().equals("/api/OTD/user/join")  ) {

        filterChain.doFilter(request, response);
        return;
    }
    // 들어온 쿠키 출력
    if (request.getCookies() != null) {
        Arrays.stream(request.getCookies()).forEach(c ->
                log.info("Cookie: {} = {}", c.getName(), c.getValue())
        );
    } else {
        log.warn("요청에 쿠키 없음");
    }
    Authentication authentication = jwtTokenManager.getAuthentication(request);
    if (authentication != null) {
        log.info("인증 성공: principal={}", authentication.getPrincipal());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    } else {
        log.warn("인증 실패 → authentication == null");
    }

    filterChain.doFilter(request, response);
}

}

package com.workmate.web.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * CSRF 토큰을 매 요청마다 강제로 렌더링해 XSRF-TOKEN 쿠키가 응답에 실리도록 한다.
 *
 * <p>CookieCsrfTokenRepository는 토큰이 실제로 "사용"될 때만 쿠키를 내려주는데,
 * SPA는 첫 GET 시점에 쿠키가 필요하다. csrfToken.getToken()을 호출해 지연 로딩된 토큰을
 * 강제로 실체화(=쿠키 기록)한다. (Spring Security SPA 연동 표준 패턴)</p>
 */
public class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            // getToken() 호출이 지연 로딩 토큰을 실체화하여 XSRF-TOKEN 쿠키를 응답에 기록한다
            csrfToken.getToken();
        }
        filterChain.doFilter(request, response);
    }
}

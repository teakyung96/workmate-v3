package com.workmate.web.global.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * WAS 위임 인증 Provider — 자격 검증은 WAS(A2)가, 세션 보유는 WEB 이 담당한다 (F1-05).
 * WAS 상태코드 매핑: 401 → BadCredentials(자격 불일치), 409 → Locked(계정 잠금).
 * 주입되는 RestClient 가 4xx 를 예외로 던지든(기본), 무동작 상태 핸들러로 통과시키든(운영 wasRestClient)
 * 동일하게 매핑되도록 두 경로를 모두 처리한다.
 */
@Slf4j
@Component
public class WasAuthenticationProvider implements AuthenticationProvider {

    private final RestClient wasRestClient;

    public WasAuthenticationProvider(RestClient wasRestClient) {
        this.wasRestClient = wasRestClient;
    }

    /**
     * 이메일·비밀번호를 WAS 로그인 API 로 위임 검증한다.
     *
     * @param authentication 사용자 입력 자격(email/password)을 담은 미인증 토큰
     * @return 인증 성공 시 LoginUser principal 과 role 권한이 채워진 토큰
     * @throws BadCredentialsException 자격 불일치(WAS 401) — 상세 사유는 노출하지 않는다 (F1.3)
     * @throws LockedException         계정 잠금(WAS 409) — WAS 가 내려준 남은 시간 메시지를 유지한다 (F1-07)
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        ResponseEntity<LoginApiResponse> entity;
        try {
            entity = wasRestClient.post()
                    .uri("/api/v1/auth/login")
                    .body(Map.of("email", email, "password", password))
                    .retrieve()
                    .toEntity(LoginApiResponse.class);
        } catch (HttpClientErrorException e) {
            // 무동작 상태 핸들러가 없는 기본 RestClient 는 4xx 를 예외로 던진다 — 본문을 그대로 해석해 매핑
            throw toAuthenticationException(e.getStatusCode(), e.getResponseBodyAs(LoginApiResponse.class));
        }

        LoginApiResponse body = entity.getBody();
        if (entity.getStatusCode() == HttpStatus.OK && body != null && body.isSuccess()) {
            LoginApiResponse.Result r = body.getResult();
            LoginUser principal = new LoginUser(r.getUserSeq(), r.getUserName(), r.getRole());
            return new UsernamePasswordAuthenticationToken(
                    principal, null, List.of(new SimpleGrantedAuthority(r.getRole())));
        }
        // 무동작 상태 핸들러를 붙인 운영 wasRestClient 는 4xx 응답도 예외 없이 여기로 들어온다
        throw toAuthenticationException(entity.getStatusCode(), body);
    }

    /** WAS 상태코드·본문을 Spring Security 예외로 변환한다 (409 → Locked, 그 외 → BadCredentials). */
    private AuthenticationException toAuthenticationException(HttpStatusCode status, LoginApiResponse body) {
        String message = (body != null && body.getMessage() != null) ? body.getMessage() : "인증에 실패했습니다.";
        if (status.value() == HttpStatus.CONFLICT.value()) {
            return new LockedException(message);        // 계정 잠금 (F1-07)
        }
        return new BadCredentialsException(message);     // 자격 불일치 — 상세 미노출 (F1.3)
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /** WAS ApiResponse&lt;LoginResponseVo&gt; 역직렬화용 내부 DTO */
    @Getter
    @Setter
    @NoArgsConstructor
    static class LoginApiResponse {
        private boolean success;
        private String message;
        private Result result;

        @Getter
        @Setter
        @NoArgsConstructor
        static class Result {
            private Long userSeq;
            private String userName;
            private String role;
        }
    }
}

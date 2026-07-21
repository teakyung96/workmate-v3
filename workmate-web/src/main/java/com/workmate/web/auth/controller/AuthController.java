package com.workmate.web.auth.controller;

import com.workmate.web.auth.service.AuthService;
import com.workmate.web.auth.vo.SignupForm;
import com.workmate.web.global.response.ApiResponse;
import com.workmate.web.global.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 REST 엔드포인트 (SPA 대응).
 *
 * <p>로그인(POST /api/auth/login)·로그아웃(POST /api/auth/logout)은 Spring Security 필터가
 * 처리하므로 여기 없다. 이 컨트롤러는 회원가입과 세션 사용자 조회만 담당한다.</p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입 — WAS 프록시. 검증은 WAS가 담당하고 실패 사유를 그대로 전달한다 (F1.3).
     *
     * @param form 가입 입력값(email·password·userName·phone)
     * @return 성공 시 success, 실패 시 WAS가 내려준 사유 메시지
     */
    @PostMapping("/signup")
    public ApiResponse<Void> signup(@RequestBody SignupForm form) {
        String errorMessage = authService.signup(form);
        return errorMessage == null ? ApiResponse.success() : ApiResponse.error(errorMessage);
    }

    /**
     * 현재 세션 사용자 조회 — SPA가 앱 부팅/새로고침 시 로그인 상태를 복원하는 데 쓴다.
     * 미인증이면 SecurityConfig의 authenticationEntryPoint가 먼저 401을 반환한다.
     *
     * @param loginUser 세션에 보관된 인증 사용자
     * @return 사용자 정보(userSeq·userName·role)
     */
    @GetMapping("/me")
    public ApiResponse<LoginUser> me(@AuthenticationPrincipal LoginUser loginUser) {
        return ApiResponse.success(loginUser);
    }
}

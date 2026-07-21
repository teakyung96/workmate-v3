package com.workmate.was.auth.service;

import com.workmate.was.auth.vo.LoginRequestVo;
import com.workmate.was.auth.vo.LoginResponseVo;
import com.workmate.was.auth.vo.SignupRequestVo;

/**
 * 인증 비즈니스 로직 인터페이스 — 가입·로그인·계정 잠금 (F1).
 */
public interface AuthService {

    /**
     * 회원가입 (F1-01): 이메일 정규화 → 정책 검증 → 중복 검사 → BCrypt 저장.
     *
     * @param request 가입 요청 VO
     */
    void signup(SignupRequestVo request);

    /**
     * 로그인 자격 검증 (F1-05~07). 세션 발급은 WEB 담당 — 여기서는 검증·잠금 판정만.
     *
     * @param request 로그인 요청 VO
     * @return 로그인 성공 응답 VO
     */
    LoginResponseVo login(LoginRequestVo request);
}

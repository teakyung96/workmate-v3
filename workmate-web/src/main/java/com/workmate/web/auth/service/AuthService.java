package com.workmate.web.auth.service;

import com.workmate.web.auth.vo.SignupForm;

/** 인증 관련 WAS API 통신 인터페이스 */
public interface AuthService {

    /**
     * 회원가입 프록시 (WAS A1 호출).
     *
     * @return 실패 메시지 — 성공이면 null
     */
    String signup(SignupForm form);
}

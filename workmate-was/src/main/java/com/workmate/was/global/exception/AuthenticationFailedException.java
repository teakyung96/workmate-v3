package com.workmate.was.global.exception;

/**
 * 인증 실패(자격 불일치) 예외 — GlobalExceptionHandler 가 401 로 변환한다.
 * 이메일/비밀번호 중 어느 쪽이 틀렸는지는 메시지에 노출하지 않는다 (F1.3).
 */
public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException(String message) {
        super(message);
    }
}

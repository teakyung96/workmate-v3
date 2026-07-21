package com.workmate.was.global.exception;

/**
 * 사용자별 요청 제한(쿼터) 초과 시 던지는 예외 — 429 로 매핑된다 (F2-11).
 */
public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException(String message) {
        super(message);
    }
}

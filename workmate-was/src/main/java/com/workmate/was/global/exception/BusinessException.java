package com.workmate.was.global.exception;

/**
 * 비즈니스 규칙 위반 시 던지는 예외.
 * 입력 형식 오류(IllegalArgumentException)와 구분하여 409로 매핑된다.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}

package com.workmate.was.global.exception;

import com.workmate.was.global.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GlobalExceptionHandler 테스트 전용 컨트롤러 (테스트 소스에만 존재).
 */
@RestController
class ExceptionTestController {

    @GetMapping("/test/business")
    ApiResponse<Void> business() {
        throw new BusinessException("비즈니스 규칙 위반");
    }

    @GetMapping("/test/illegal")
    ApiResponse<Void> illegal() {
        throw new IllegalArgumentException("입력값 오류");
    }
}

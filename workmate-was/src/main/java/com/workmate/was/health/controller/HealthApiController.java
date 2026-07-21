package com.workmate.was.health.controller;

import com.workmate.was.global.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * WAS 기동 상태 확인용 헬스체크 API.
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthApiController {

    /**
     * 헬스체크.
     *
     * @return 공통 응답 포맷에 "was" 문자열
     */
    @GetMapping
    public ApiResponse<String> health() {
        return ApiResponse.success("was");
    }
}

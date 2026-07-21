package com.workmate.web.global.exception;

import com.workmate.web.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

/**
 * WEB 전역 예외 핸들러.
 * 프록시 컨트롤러들이 개별 try-catch 하지 않도록, WAS 중계 과정에서 발생하는 공통 예외를 한곳에서 처리한다.
 * - ResourceAccessException: 502 (WAS 자체에 연결 불가 — 다운/네트워크 단절 등)
 *   ※ WAS 가 돌려주는 4xx/5xx 응답은 RestClientConfig 의 무동작 상태 핸들러 덕분에
 *     예외가 아니라 상태코드·본문 그대로 통과하므로 여기서 잡히지 않는다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * WAS 연결 불가 시 502 와 공통 에러 JSON 을 화면에 내려준다.
     *
     * @param e RestClient 가 WAS 에 접속하지 못했을 때 던지는 예외
     * @return 502 상태 + ApiResponse.error 포맷 JSON
     */
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleWasUnreachable(ResourceAccessException e) {
        log.error("WAS 연결 실패", e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.error("백엔드(WAS) 서버에 연결할 수 없습니다. 서버 상태를 확인해 주세요."));
    }
}

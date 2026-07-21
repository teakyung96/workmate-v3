package com.workmate.web.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * WEB 공통 응답 래퍼 클래스.
 * WEB 레이어가 자체적으로 생성하는 응답(예: WAS 연결 실패 등)을 WAS 의 ApiResponse 와
 * 동일한 포맷(success/message/result)으로 화면에 내려주기 위한 클래스다.
 * (WEB/WAS 는 별도 부트앱이라 클래스를 공유할 수 없어 동일 구조를 복제한다)
 *
 * @param <T> 실제 응답 데이터 타입
 */
@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    /** 성공 여부 */
    private boolean success;

    /** 결과 메시지 (성공/실패 여부 설명) */
    private String message;

    /** 실제 반환 데이터 객체 */
    private T result;

    /**
     * 성공 응답 생성 (데이터 포함).
     *
     * @param result 반환할 데이터
     * @return success=true, message="success" 응답
     */
    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(true, "success", result);
    }

    /**
     * 성공 응답 생성 (데이터 없음).
     *
     * @return success=true, message="success", result=null 응답
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, "success", null);
    }

    /**
     * 에러 응답 생성.
     *
     * @param message 에러 메시지
     * @return success=false 응답 (result=null)
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}

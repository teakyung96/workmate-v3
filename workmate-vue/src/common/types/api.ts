/**
 * WAS/WEB 공통 응답 래퍼 타입.
 * 백엔드 `ApiResponse { success, message, result }` 포맷과 1:1 대응한다.
 */
export interface ApiResponse<T = unknown> {
    /** 성공 여부 */
    success: boolean
    /** 결과 메시지 (성공 시 "success", 실패 시 사유) */
    message: string
    /** 실제 반환 데이터 */
    result: T
}

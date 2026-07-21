import axios from 'axios'

/**
 * 서버 오류 응답에서 사용자에게 보여줄 메시지를 뽑아낸다.
 * axios 에러면 백엔드 ApiResponse.message를, 아니면 fallback을 반환한다.
 *
 * @param error 발생한 예외
 * @param fallback 메시지를 못 찾았을 때 기본 문구
 */
export function extractErrorMessage(error: unknown, fallback: string): string {
    if (axios.isAxiosError(error)) {
        return error.response?.data?.message ?? fallback
    }
    return fallback
}

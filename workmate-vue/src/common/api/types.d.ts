import 'axios'

/**
 * axios 요청 설정 확장.
 * `skipAuthRedirect`: 401이어도 전역 로그인 리다이렉트를 건너뛴다 (세션확인 요청 등에 사용).
 */
declare module 'axios' {
    export interface AxiosRequestConfig {
        skipAuthRedirect?: boolean
    }
}

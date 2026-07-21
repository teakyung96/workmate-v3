import axios from 'axios'

/**
 * 모든 모듈이 공유하는 HTTP 클라이언트 (얇은 WEB의 /api 로만 통신).
 *
 * - `withCredentials`: 세션 쿠키(JSESSIONID)를 요청에 동봉
 * - `xsrfCookieName`/`xsrfHeaderName`: 서버가 내려준 XSRF-TOKEN 쿠키를 읽어
 *   상태변경 요청 시 X-XSRF-TOKEN 헤더로 자동 전송 (쿠키 기반 CSRF)
 */
const client = axios.create({
    baseURL: '/api',
    withCredentials: true,
    xsrfCookieName: 'XSRF-TOKEN',
    xsrfHeaderName: 'X-XSRF-TOKEN',
})

// 401(미인증/세션만료) 발생 시 실행할 핸들러 — main.ts가 store·router와 연결한다.
// (client가 store/router를 직접 import하면 순환참조가 생기므로 콜백 주입 방식을 쓴다)
let unauthorizedHandler: (() => void) | null = null

/**
 * 401 응답 시 실행할 전역 핸들러를 등록한다 (앱 부팅 시 1회).
 * @param handler auth store를 비우고 로그인 화면으로 유도하는 콜백
 */
export function setUnauthorizedHandler(handler: () => void): void {
    unauthorizedHandler = handler
}

// 응답 인터셉터: 401이면 전역 핸들러 호출. 단 skipAuthRedirect가 붙은 요청은 제외
// (예: 부팅 시 /auth/me 세션확인 — 미로그인 401은 정상 흐름이라 리다이렉트하지 않음)
client.interceptors.response.use(
    (response) => response,
    (error) => {
        const status = error.response?.status
        const skip = error.config?.skipAuthRedirect === true
        if (status === 401 && !skip) {
            unauthorizedHandler?.()
        }
        return Promise.reject(error)
    },
)

export default client

import client from '@/common/api/client'
import type { ApiResponse } from '@/common/types/api'
import type { LoginUser, SignupRequest } from '../types'

/**
 * 인증 API (얇은 WEB의 /api/auth/* 호출).
 * 계층 규칙: HTTP 통신만 담당하고 비즈니스 로직/상태변경은 하지 않는다.
 */
export const authApi = {
    /**
     * 로그인 — Spring Security 폼 로그인 필터가 처리하므로 **form-urlencoded**로 보낸다.
     * @returns 로그인 사용자 정보
     */
    async login(email: string, password: string): Promise<LoginUser> {
        const body = new URLSearchParams({ email, password })
        const { data } = await client.post<ApiResponse<LoginUser>>('/auth/login', body, {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            // 로그인 실패(401)는 화면에서 메시지로 처리하므로 전역 리다이렉트 제외
            skipAuthRedirect: true,
        })
        return data.result
    },

    /**
     * 회원가입 (JSON). 검증은 WAS가 담당하고 실패 사유는 응답 message로 온다.
     * @returns 공통 응답 (success=false면 message에 사유)
     */
    async signup(payload: SignupRequest): Promise<ApiResponse<void>> {
        const { data } = await client.post<ApiResponse<void>>('/auth/signup', payload)
        return data
    },

    /**
     * 현재 세션 사용자 조회 (앱 부팅/새로고침 시 세션 복원용).
     * 미로그인이면 401이므로 null로 정규화한다 (전역 리다이렉트 제외).
     */
    async me(): Promise<LoginUser | null> {
        try {
            const { data } = await client.get<ApiResponse<LoginUser>>('/auth/me', {
                skipAuthRedirect: true,
            })
            return data.result
        } catch {
            return null
        }
    },

    /** 로그아웃 (세션 무효화) */
    async logout(): Promise<void> {
        await client.post('/auth/logout')
    },
}

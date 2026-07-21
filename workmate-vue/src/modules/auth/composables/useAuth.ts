import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { extractErrorMessage } from '@/common/utils/error'
import { authApi } from '../api/auth.api'
import { useAuthStore } from '../stores/auth.store'
import type { SignupRequest } from '../types'

/**
 * 인증 화면(로그인·회원가입)의 로직을 담는 composable (≈ Service).
 * 화면(View)은 이 함수들만 호출하고 api를 직접 부르지 않는다.
 */
export function useAuth() {
    const store = useAuthStore()
    const router = useRouter()
    const route = useRoute()

    const loading = ref(false)
    const errorMessage = ref('')

    /**
     * 로그인 시도. 성공 시 원래 목적지(redirect 쿼리) 또는 /chat 으로 이동.
     */
    async function login(email: string, password: string): Promise<void> {
        loading.value = true
        errorMessage.value = ''
        try {
            const user = await authApi.login(email, password)
            store.setUser(user)
            const redirect = (route.query.redirect as string) || '/chat'
            await router.replace(redirect)
        } catch (error) {
            errorMessage.value = extractErrorMessage(error, '로그인에 실패했습니다.')
        } finally {
            loading.value = false
        }
    }

    /**
     * 회원가입 시도. 성공 시 로그인 화면으로 이동('가입 완료' 안내 플래그 전달).
     * @returns 성공 여부 (실패 시 errorMessage에 사유 세팅)
     */
    async function signup(payload: SignupRequest): Promise<boolean> {
        loading.value = true
        errorMessage.value = ''
        try {
            const res = await authApi.signup(payload)
            if (!res.success) {
                errorMessage.value = res.message
                return false
            }
            await router.replace({ name: 'login', query: { signup: 'success' } })
            return true
        } catch (error) {
            errorMessage.value = extractErrorMessage(error, '회원가입에 실패했습니다.')
            return false
        } finally {
            loading.value = false
        }
    }

    /** 로그아웃 후 로그인 화면으로 이동 */
    async function logout(): Promise<void> {
        try {
            await authApi.logout()
        } finally {
            store.clear()
            await router.replace({ name: 'login' })
        }
    }

    return { loading, errorMessage, login, signup, logout }
}

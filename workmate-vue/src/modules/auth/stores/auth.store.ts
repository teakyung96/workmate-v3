import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { authApi } from '../api/auth.api'
import type { LoginUser } from '../types'

/**
 * 인증 전역 상태 (헤더·가드·여러 화면이 공유하므로 store로 승격).
 * 서버 세션이 진실의 원천이고, 이 store는 그 사본을 들고 있다.
 */
export const useAuthStore = defineStore('auth', () => {
    /** 로그인 사용자 (미로그인 시 null) */
    const user = ref<LoginUser | null>(null)
    /** 부팅 시 세션 확인(/me)을 이미 마쳤는지 — 가드가 중복 호출을 피하는 데 사용 */
    const resolved = ref(false)

    const isAuthenticated = computed(() => user.value !== null)
    const isAdmin = computed(() => {
        const role = user.value?.role
        return role === 'ADMIN' || role === 'ROLE_ADMIN'
    })

    /** 로그인 성공 등으로 사용자를 세팅 */
    function setUser(value: LoginUser | null): void {
        user.value = value
    }

    /** 상태 비우기 (로그아웃·401) */
    function clear(): void {
        user.value = null
    }

    /**
     * 세션 복원 — 앱 부팅 시 1회 /me 를 호출해 로그인 상태를 되살린다.
     * 이미 확인했으면 재호출하지 않는다.
     */
    async function resolveSession(): Promise<void> {
        if (resolved.value) return
        user.value = await authApi.me()
        resolved.value = true
    }

    return { user, resolved, isAuthenticated, isAdmin, setUser, clear, resolveSession }
})

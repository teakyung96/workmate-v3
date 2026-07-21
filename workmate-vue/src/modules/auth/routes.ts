import type { RouteRecordRaw } from 'vue-router'

/**
 * auth 모듈 라우트 — 각 모듈이 자기 라우트를 소유하고 router/index.ts가 취합한다.
 * meta.public: 인증 없이 접근 가능 / meta.layout: 'auth'(단독 카드) 레이아웃 사용
 */
export const authRoutes: RouteRecordRaw[] = [
    {
        path: '/login',
        name: 'login',
        component: () => import('./views/LoginPage.vue'),
        meta: { public: true, layout: 'auth' },
    },
    {
        path: '/signup',
        name: 'signup',
        component: () => import('./views/SignupPage.vue'),
        meta: { public: true, layout: 'auth' },
    },
]

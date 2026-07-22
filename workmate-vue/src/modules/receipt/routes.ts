import type { RouteRecordRaw } from 'vue-router'

/**
 * receipt 모듈 라우트 (인증 필요 — 전역 가드가 보호).
 * 한 화면 안에서 분석/이력 탭을 전환한다.
 */
export const receiptRoutes: RouteRecordRaw[] = [
    {
        path: '/receipt',
        name: 'receipt',
        component: () => import('./views/ReceiptPage.vue'),
    },
]

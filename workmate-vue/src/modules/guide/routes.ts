import type { RouteRecordRaw } from 'vue-router'

/**
 * guide 모듈 라우트 (인증 필요 — 전역 가드가 보호).
 * /guide/new 는 정적 경로라 /guide/:id 보다 우선 매칭된다.
 */
export const guideRoutes: RouteRecordRaw[] = [
    {
        path: '/guide',
        name: 'guide-list',
        component: () => import('./views/GuideListPage.vue'),
    },
    {
        path: '/guide/new',
        name: 'guide-new',
        component: () => import('./views/GuideFormPage.vue'),
    },
    {
        path: '/guide/:id',
        name: 'guide-detail',
        component: () => import('./views/GuideDetailPage.vue'),
    },
    {
        path: '/guide/:id/edit',
        name: 'guide-edit',
        component: () => import('./views/GuideFormPage.vue'),
    },
]

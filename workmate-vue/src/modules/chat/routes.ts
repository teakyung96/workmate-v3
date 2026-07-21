import type { RouteRecordRaw } from 'vue-router'

/**
 * chat 모듈 라우트. 현재는 로그인 성공 후 랜딩용 플레이스홀더이며,
 * 실제 채팅(SSE) 화면은 이후 구현 단계에서 채운다.
 */
export const chatRoutes: RouteRecordRaw[] = [
    {
        path: '/chat',
        name: 'chat',
        component: () => import('./views/ChatPage.vue'),
    },
]

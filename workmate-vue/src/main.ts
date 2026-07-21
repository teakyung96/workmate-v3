import './styles/index.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { setUnauthorizedHandler } from './common/api/client'
import { useAuthStore } from './modules/auth/stores/auth.store'

const app = createApp(App)

app.use(createPinia())
app.use(router)

// 401(세션 만료/미인증) 전역 처리: auth store를 비우고 로그인 화면으로 유도.
// (pinia 설치 이후이므로 useAuthStore 호출 가능)
setUnauthorizedHandler(() => {
    const auth = useAuthStore()
    auth.clear()
    if (router.currentRoute.value.name !== 'login') {
        router.replace({ name: 'login' })
    }
})

app.mount('#app')

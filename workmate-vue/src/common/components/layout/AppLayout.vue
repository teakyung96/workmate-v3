<script setup lang="ts">
/**
 * 앱 기본 레이아웃 — 상단 헤더(앱명·사용자·로그아웃) + 본문.
 * 사이드바 등 세부 구성은 이후 화면 구현 단계에서 확장한다.
 */
import { RouterLink } from 'vue-router'
import { Button } from '@/common/components/ui/button'
import { useAuth } from '@/modules/auth/composables/useAuth'
import { useAuthStore } from '@/modules/auth/stores/auth.store'

const auth = useAuthStore()
const { logout } = useAuth()

// 헤더 네비게이션 항목 (채팅·가이드 — 이후 영수증·관리자 추가)
const navItems = [
    { name: 'chat', label: '채팅' },
    { name: 'guide-list', label: '가이드' },
]
</script>

<template>
    <div class="flex min-h-screen flex-col">
        <header class="flex h-14 items-center justify-between border-b px-4">
            <div class="flex items-center gap-6">
                <span class="font-semibold">Workmate</span>
                <nav class="flex items-center gap-4">
                    <RouterLink
                        v-for="item in navItems"
                        :key="item.name"
                        :to="{ name: item.name }"
                        class="text-sm text-muted-foreground hover:text-foreground"
                        active-class="!text-foreground font-medium"
                    >
                        {{ item.label }}
                    </RouterLink>
                </nav>
            </div>
            <div class="flex items-center gap-3">
                <span v-if="auth.user" class="text-sm text-muted-foreground">
                    {{ auth.user.userName }} 님
                </span>
                <Button variant="outline" size="sm" @click="logout">로그아웃</Button>
            </div>
        </header>
        <main class="flex-1 p-4">
            <slot />
        </main>
    </div>
</template>

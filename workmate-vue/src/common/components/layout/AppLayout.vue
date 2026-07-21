<script setup lang="ts">
/**
 * 앱 기본 레이아웃 — 상단 헤더(앱명·사용자·로그아웃) + 본문.
 * 사이드바 등 세부 구성은 이후 화면 구현 단계에서 확장한다.
 */
import { Button } from '@/common/components/ui/button'
import { useAuth } from '@/modules/auth/composables/useAuth'
import { useAuthStore } from '@/modules/auth/stores/auth.store'

const auth = useAuthStore()
const { logout } = useAuth()
</script>

<template>
    <div class="flex min-h-screen flex-col">
        <header class="flex h-14 items-center justify-between border-b px-4">
            <span class="font-semibold">Workmate</span>
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

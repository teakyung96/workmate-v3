<script setup lang="ts">
/** 채팅 홈 플레이스홀더 — 로그인 성공 후 랜딩. 실제 SSE 채팅은 이후 구현. */
import { useAuthStore } from '@/modules/auth/stores/auth.store'

const auth = useAuthStore()
</script>

<template>
    <div class="mx-auto max-w-2xl py-10 text-center">
        <h1 class="text-2xl font-semibold">채팅 (준비 중)</h1>
        <p class="mt-2 text-muted-foreground">
            {{ auth.user?.userName }} 님, 로그인되었습니다. 채팅 화면은 곧 제공됩니다.
        </p>
    </div>
</template>

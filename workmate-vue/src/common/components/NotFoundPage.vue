<script setup lang="ts">
/** 404 — 존재하지 않는 경로 캐치올 화면 */
import { RouterLink } from 'vue-router'
import { Button } from '@/common/components/ui/button'
</script>

<template>
    <div class="flex min-h-[60vh] flex-col items-center justify-center gap-4 text-center">
        <p class="text-6xl font-bold text-muted-foreground">404</p>
        <p class="text-muted-foreground">요청하신 페이지를 찾을 수 없습니다.</p>
        <Button as-child>
            <RouterLink to="/chat">홈으로</RouterLink>
        </Button>
    </div>
</template>

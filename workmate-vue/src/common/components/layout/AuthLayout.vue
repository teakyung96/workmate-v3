<script setup lang="ts">
/**
 * 인증 화면 레이아웃 — 헤더·사이드바 없이 중앙 단독 카드만 배치 (로그인·회원가입).
 * 실제 화면은 <slot/>(=RouterView)으로 주입된다.
 */
</script>

<template>
    <div class="flex min-h-screen items-center justify-center bg-muted/30 p-4">
        <slot />
    </div>
</template>

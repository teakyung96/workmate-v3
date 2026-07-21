<script setup lang="ts">
/**
 * 앱 기본 레이아웃 — 좌측 공통 사이드바 + 우측 본문(RouterView).
 */
import AppSidebar from './AppSidebar.vue'
</script>

<template>
    <div class="flex h-screen">
        <AppSidebar />
        <main class="min-w-0 flex-1 overflow-hidden">
            <slot />
        </main>
    </div>
</template>

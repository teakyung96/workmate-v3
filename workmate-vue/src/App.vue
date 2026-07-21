<script setup lang="ts">
/**
 * 루트 셸 — 현재 라우트의 meta.layout에 따라 레이아웃을 선택하고
 * 그 안에 RouterView(실제 화면)를 끼운다.
 */
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import AppLayout from '@/common/components/layout/AppLayout.vue'
import AuthLayout from '@/common/components/layout/AuthLayout.vue'

const route = useRoute()
const layout = computed(() => (route.meta.layout === 'auth' ? AuthLayout : AppLayout))
</script>

<template>
    <component :is="layout">
        <RouterView />
    </component>
</template>

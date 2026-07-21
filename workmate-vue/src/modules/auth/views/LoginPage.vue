<script setup lang="ts">
/**
 * 로그인 화면 (/login) — 단독 카드 레이아웃.
 * 미입력 시 버튼 비활성, 실패/잠금 사유는 서버 메시지를 그대로 표시(어느 쪽 오류인지 노출 안 함).
 */
import { computed, ref } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { Button } from '@/common/components/ui/button'
import { Input } from '@/common/components/ui/input'
import { Label } from '@/common/components/ui/label'
import { Alert, AlertDescription } from '@/common/components/ui/alert'
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from '@/common/components/ui/card'
import { useAuth } from '../composables/useAuth'

const route = useRoute()
const { loading, errorMessage, login } = useAuth()

const email = ref('')
const password = ref('')

// 회원가입 직후 이동해온 경우 안내 표시
const signupSuccess = computed(() => route.query.signup === 'success')
// 미입력 시 로그인 버튼 비활성 (F1-05)
const canSubmit = computed(
    () => email.value.trim() !== '' && password.value !== '' && !loading.value,
)

function onSubmit(): void {
    if (!canSubmit.value) return
    login(email.value.trim(), password.value)
}
</script>

<template>
    <Card class="w-full max-w-sm">
        <CardHeader>
            <CardTitle class="text-2xl">로그인</CardTitle>
            <CardDescription>Workmate 업무 비서에 로그인하세요.</CardDescription>
        </CardHeader>
        <CardContent>
            <form class="flex flex-col gap-4" @submit.prevent="onSubmit">
                <Alert v-if="signupSuccess">
                    <AlertDescription>회원가입이 완료되었습니다. 로그인해 주세요.</AlertDescription>
                </Alert>

                <div class="flex flex-col gap-2">
                    <Label for="email">이메일</Label>
                    <Input
                        id="email"
                        v-model="email"
                        type="email"
                        autocomplete="email"
                        placeholder="you@example.com"
                    />
                </div>

                <div class="flex flex-col gap-2">
                    <Label for="password">비밀번호</Label>
                    <Input
                        id="password"
                        v-model="password"
                        type="password"
                        autocomplete="current-password"
                    />
                </div>

                <Alert v-if="errorMessage" variant="destructive">
                    <AlertDescription>{{ errorMessage }}</AlertDescription>
                </Alert>

                <Button type="submit" :disabled="!canSubmit">
                    {{ loading ? '로그인 중…' : '로그인' }}
                </Button>
            </form>
        </CardContent>
        <CardFooter class="justify-center gap-1 text-sm text-muted-foreground">
            아직 계정이 없나요?
            <RouterLink to="/signup" class="font-medium text-primary hover:underline">
                회원가입
            </RouterLink>
        </CardFooter>
    </Card>
</template>

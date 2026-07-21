<script setup lang="ts">
/**
 * 회원가입 화면 (/signup) — 단독 카드 레이아웃.
 * 비밀번호 정책(8자+영문+숫자+특수)·확인 일치를 실시간 검증하고, 서버가 최종 재검증한다 (F1-04).
 */
import { computed, ref } from 'vue'
import { RouterLink } from 'vue-router'
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

const { loading, errorMessage, signup } = useAuth()

const email = ref('')
const userName = ref('')
const phone = ref('')
const password = ref('')
const passwordConfirm = ref('')

// 비밀번호 정책: 8자 이상 + 영문 + 숫자 + 특수문자
const PASSWORD_POLICY = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/
const EMAIL_FORMAT = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

const emailValid = computed(() => EMAIL_FORMAT.test(email.value))
const nameValid = computed(() => userName.value.trim() !== '')
const passwordValid = computed(() => PASSWORD_POLICY.test(password.value))
const confirmValid = computed(
    () => passwordConfirm.value !== '' && password.value === passwordConfirm.value,
)

const canSubmit = computed(
    () =>
        emailValid.value &&
        nameValid.value &&
        passwordValid.value &&
        confirmValid.value &&
        !loading.value,
)

function onSubmit(): void {
    if (!canSubmit.value) return
    signup({
        email: email.value.trim(),
        password: password.value,
        userName: userName.value.trim(),
        phone: phone.value.trim(),
    })
}
</script>

<template>
    <Card class="w-full max-w-sm">
        <CardHeader>
            <CardTitle class="text-2xl">회원가입</CardTitle>
            <CardDescription>업무 비서 사용을 위한 계정을 만듭니다.</CardDescription>
        </CardHeader>
        <CardContent>
            <form class="flex flex-col gap-4" @submit.prevent="onSubmit">
                <div class="flex flex-col gap-2">
                    <Label for="email">이메일</Label>
                    <Input
                        id="email"
                        v-model="email"
                        type="email"
                        autocomplete="email"
                        placeholder="you@example.com"
                    />
                    <p v-if="email !== '' && !emailValid" class="text-xs text-destructive">
                        올바른 이메일 형식이 아닙니다.
                    </p>
                </div>

                <div class="flex flex-col gap-2">
                    <Label for="userName">이름</Label>
                    <Input id="userName" v-model="userName" autocomplete="name" />
                </div>

                <div class="flex flex-col gap-2">
                    <Label for="phone">전화번호</Label>
                    <Input
                        id="phone"
                        v-model="phone"
                        type="tel"
                        autocomplete="tel"
                        placeholder="010-0000-0000"
                    />
                </div>

                <div class="flex flex-col gap-2">
                    <Label for="password">비밀번호</Label>
                    <Input
                        id="password"
                        v-model="password"
                        type="password"
                        autocomplete="new-password"
                    />
                    <p v-if="password !== '' && !passwordValid" class="text-xs text-destructive">
                        8자 이상, 영문·숫자·특수문자를 모두 포함해야 합니다.
                    </p>
                </div>

                <div class="flex flex-col gap-2">
                    <Label for="passwordConfirm">비밀번호 확인</Label>
                    <Input
                        id="passwordConfirm"
                        v-model="passwordConfirm"
                        type="password"
                        autocomplete="new-password"
                    />
                    <p
                        v-if="passwordConfirm !== '' && !confirmValid"
                        class="text-xs text-destructive"
                    >
                        비밀번호가 일치하지 않습니다.
                    </p>
                </div>

                <Alert v-if="errorMessage" variant="destructive">
                    <AlertDescription>{{ errorMessage }}</AlertDescription>
                </Alert>

                <Button type="submit" :disabled="!canSubmit">
                    {{ loading ? '가입 중…' : '가입하기' }}
                </Button>
            </form>
        </CardContent>
        <CardFooter class="justify-center gap-1 text-sm text-muted-foreground">
            이미 계정이 있나요?
            <RouterLink to="/login" class="font-medium text-primary hover:underline"
                >로그인</RouterLink
            >
        </CardFooter>
    </Card>
</template>

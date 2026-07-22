<script setup lang="ts">
/**
 * 복사 가능한 편집 필드 — 라벨 + 입력창 + 복사 버튼.
 * 값 수정은 v-model, 복사 버튼은 현재 값을 클립보드에 담고 잠시 "복사됨"을 표시한다(전역 토스트 대신 로컬 피드백).
 */
import { ref } from 'vue'
import { Check, Copy } from 'lucide-vue-next'
import { Input } from '@/common/components/ui/input'
import { Label } from '@/common/components/ui/label'

const props = defineProps<{
    modelValue: string
    label: string
    placeholder?: string
    /** 숫자 전용 입력 힌트 (모바일 키패드) */
    numeric?: boolean
}>()
const emit = defineEmits<{ 'update:modelValue': [value: string] }>()

const copied = ref(false)

/** 현재 값을 클립보드에 복사하고 1.5초간 "복사됨" 표시 */
async function copy(): Promise<void> {
    try {
        await navigator.clipboard.writeText(props.modelValue ?? '')
        copied.value = true
        setTimeout(() => (copied.value = false), 1500)
    } catch {
        // 클립보드 접근 불가(비보안 컨텍스트 등)면 조용히 무시
    }
}
</script>

<template>
    <div class="flex flex-col gap-1.5">
        <Label>{{ label }}</Label>
        <div class="flex items-center gap-2">
            <Input
                :model-value="modelValue"
                :placeholder="placeholder"
                :inputmode="numeric ? 'numeric' : undefined"
                @update:model-value="emit('update:modelValue', String($event))"
            />
            <button
                type="button"
                class="flex shrink-0 items-center gap-1 rounded-md border px-2 py-2 text-xs text-muted-foreground hover:bg-accent hover:text-foreground"
                :title="copied ? '복사됨' : '복사'"
                @click="copy"
            >
                <Check v-if="copied" class="size-4 text-green-600" />
                <Copy v-else class="size-4" />
            </button>
        </div>
        <slot name="hint" />
    </div>
</template>

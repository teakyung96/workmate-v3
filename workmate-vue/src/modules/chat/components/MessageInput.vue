<script setup lang="ts">
/**
 * 메시지 입력창 — 멀티라인. Enter 전송 / Shift+Enter 줄바꿈, 한글 IME 조합 중엔 전송 안 함.
 * 빈 입력이나 스트리밍 중엔 전송 비활성.
 */
import { ref } from 'vue'
import { BookText, SendHorizontal } from 'lucide-vue-next'
import { Button } from '@/common/components/ui/button'
import { Textarea } from '@/common/components/ui/textarea'

const props = defineProps<{ disabled?: boolean; ragMode?: boolean }>()
const emit = defineEmits<{ send: [text: string]; 'update:ragMode': [value: boolean] }>()

const text = ref('')

function submit(): void {
    const trimmed = text.value.trim()
    if (trimmed === '' || props.disabled) return
    emit('send', trimmed)
    text.value = ''
}

function onKeydown(event: KeyboardEvent): void {
    // IME 조합 중(한글 입력)의 Enter는 무시
    if (event.key === 'Enter' && !event.shiftKey && !event.isComposing) {
        event.preventDefault()
        submit()
    }
}
</script>

<template>
    <div class="mx-auto w-full max-w-3xl px-4 pb-4">
        <div class="flex items-end gap-2 rounded-2xl border bg-background p-2 shadow-sm">
            <!-- 가이드 참고(RAG) 토글 — 켜면 답변이 가이드 문서를 검색해 출처와 함께 응답 -->
            <button
                type="button"
                class="flex shrink-0 items-center gap-1 self-center rounded-full border px-2.5 py-1.5 text-xs font-medium transition-colors"
                :class="
                    ragMode
                        ? 'border-primary bg-primary/10 text-primary'
                        : 'border-transparent text-muted-foreground hover:bg-accent'
                "
                :title="ragMode ? '가이드 참고 켜짐' : '가이드 참고 꺼짐'"
                :aria-pressed="ragMode"
                @click="emit('update:ragMode', !ragMode)"
            >
                <BookText class="size-4" />
                가이드 참고
            </button>
            <Textarea
                v-model="text"
                rows="1"
                placeholder="메시지를 입력하세요…"
                class="max-h-40 min-h-0 resize-none border-0 py-1.5 shadow-none focus-visible:ring-0"
                @keydown="onKeydown"
            />
            <Button
                size="icon"
                class="shrink-0"
                :disabled="disabled || text.trim() === ''"
                @click="submit"
            >
                <SendHorizontal class="size-4" />
            </Button>
        </div>
    </div>
</template>

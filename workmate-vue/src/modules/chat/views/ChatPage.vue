<script setup lang="ts">
/**
 * 채팅 화면 (/chat) — 빈 상태(방 미생성)와 대화 상태.
 * 방 목록·선택은 공통 사이드바가 담당하고, 여기선 메시지 영역 + 입력창만 그린다.
 */
import { computed } from 'vue'
import { useChatStore } from '../stores/chat.store'
import MessageList from '../components/MessageList.vue'
import MessageInput from '../components/MessageInput.vue'

const chat = useChatStore()
const isEmpty = computed(() => chat.messages.length === 0)

function onSend(text: string): void {
    chat.sendMessage(text)
}
</script>

<template>
    <div class="flex h-full flex-col">
        <!-- 빈 상태: 중앙 안내 + 입력창 -->
        <div v-if="isEmpty" class="flex flex-1 flex-col items-center justify-center">
            <h1 class="mb-8 text-2xl font-semibold text-muted-foreground">무엇을 도와드릴까요?</h1>
            <MessageInput
                v-model:rag-mode="chat.ragMode"
                :disabled="chat.streaming"
                @send="onSend"
            />
        </div>

        <!-- 대화 상태: 메시지 목록 + 하단 입력창 -->
        <template v-else>
            <MessageList :messages="chat.messages" />
            <MessageInput
                v-model:rag-mode="chat.ragMode"
                :disabled="chat.streaming"
                @send="onSend"
            />
        </template>
    </div>
</template>

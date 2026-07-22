import { defineStore } from 'pinia'
import { ref } from 'vue'
import { chatApi } from '../api/chat.api'
import type { ChatMessage, ChatRoom } from '../types'

/**
 * 채팅 전역 상태 (SSE 수신 중 화면을 이동해도 유지되어야 하므로 store로 관리).
 * 방 목록·현재 방·메시지·스트리밍 상태를 보유하고, 전송/수신을 오케스트레이션한다.
 */
export const useChatStore = defineStore('chat', () => {
    const rooms = ref<ChatRoom[]>([])
    const currentRoomSeq = ref<number | null>(null)
    const messages = ref<ChatMessage[]>([])
    const streaming = ref(false)
    const roomsLoaded = ref(false)
    // RAG 모드 — 켜면 전송 시 가이드 문서 검색 근거를 요청한다(출처 뱃지로 표시). 대화 내내 유지되는 선호값이라 store에 둔다
    const ragMode = ref(false)

    /** 방 목록 로드 (C1) */
    async function loadRooms(): Promise<void> {
        rooms.value = await chatApi.rooms()
        roomsLoaded.value = true
    }

    /** 새 채팅 시작 — 현재 방/메시지 초기화 (빈 상태) */
    function startNewChat(): void {
        if (streaming.value) return
        currentRoomSeq.value = null
        messages.value = []
    }

    /** 방 선택 → 대화 이력 로드 (C2) */
    async function selectRoom(roomSeq: number): Promise<void> {
        if (streaming.value) return
        currentRoomSeq.value = roomSeq
        messages.value = await chatApi.messages(roomSeq)
    }

    /** 방 삭제 (C4). 현재 보던 방이면 새 채팅 상태로 전환 */
    async function deleteRoom(roomSeq: number): Promise<void> {
        await chatApi.deleteRoom(roomSeq)
        rooms.value = rooms.value.filter((r) => r.roomSeq !== roomSeq)
        if (currentRoomSeq.value === roomSeq) startNewChat()
    }

    /**
     * 메시지 전송 + SSE 스트리밍 수신 (C3).
     * 사용자 메시지와 빈 AI 메시지를 먼저 넣고, 토큰이 올 때마다 AI 메시지에 이어붙인다.
     */
    async function sendMessage(text: string): Promise<void> {
        const trimmed = text.trim()
        if (streaming.value || trimmed === '') return

        messages.value.push({ role: 'user', content: trimmed })
        messages.value.push({ role: 'assistant', content: '', streaming: true })
        // 배열에 들어간 반응형 프록시를 잡아야 이후 변경이 화면에 반영된다
        const ai = messages.value[messages.value.length - 1]!
        streaming.value = true

        try {
            await chatApi.stream(
                { roomSeq: currentRoomSeq.value, message: trimmed, ragMode: ragMode.value },
                {
                    onMeta: (d) => {
                        // 새 방 생성 시: 현재 방으로 지정 + 목록 맨 위에 추가
                        currentRoomSeq.value = d.roomSeq
                        if (!rooms.value.some((r) => r.roomSeq === d.roomSeq)) {
                            rooms.value.unshift({
                                roomSeq: d.roomSeq,
                                title: d.title,
                                createdAt: new Date().toISOString(),
                            })
                        }
                    },
                    onToken: (d) => {
                        ai.content += d.delta
                    },
                    onSource: (d) => {
                        ;(ai.sources ??= []).push(d)
                    },
                    onDone: (d) => {
                        ai.streaming = false
                        ai.messageSeq = d.messageSeq
                    },
                    onError: (d) => {
                        ai.streaming = false
                        ai.error = true
                        if (!ai.content) ai.content = d.message
                    },
                },
            )
        } catch {
            ai.error = true
            if (!ai.content) ai.content = '응답 중 오류가 발생했습니다.'
        } finally {
            streaming.value = false
            ai.streaming = false
        }
    }

    return {
        rooms,
        currentRoomSeq,
        messages,
        streaming,
        roomsLoaded,
        ragMode,
        loadRooms,
        startNewChat,
        selectRoom,
        deleteRoom,
        sendMessage,
    }
})

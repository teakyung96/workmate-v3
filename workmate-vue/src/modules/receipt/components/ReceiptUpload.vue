<script setup lang="ts">
/**
 * 영수증 이미지 업로드 영역 — 드래그앤드롭 + 클릭 선택.
 * 실제 형식·크기 검증은 상위(useReceiptAnalyze.selectFile)에서 하고, 여기선 File만 올려보낸다.
 */
import { ref } from 'vue'
import { ImageUp } from 'lucide-vue-next'

const emit = defineEmits<{ select: [file: File] }>()

const inputEl = ref<HTMLInputElement | null>(null)
const dragging = ref(false)

function openPicker(): void {
    inputEl.value?.click()
}

function onPicked(event: Event): void {
    const target = event.target as HTMLInputElement
    const file = target.files?.[0]
    if (file) emit('select', file)
    target.value = '' // 같은 파일 재선택 허용
}

function onDrop(event: DragEvent): void {
    dragging.value = false
    const file = event.dataTransfer?.files?.[0]
    if (file) emit('select', file)
}
</script>

<template>
    <div
        class="flex cursor-pointer flex-col items-center justify-center gap-3 rounded-xl border-2 border-dashed px-6 py-16 text-center transition-colors"
        :class="
            dragging
                ? 'border-primary bg-primary/5'
                : 'border-muted-foreground/25 hover:border-muted-foreground/40'
        "
        role="button"
        tabindex="0"
        @click="openPicker"
        @keydown.enter="openPicker"
        @dragover.prevent="dragging = true"
        @dragleave.prevent="dragging = false"
        @drop.prevent="onDrop"
    >
        <ImageUp class="size-10 text-muted-foreground" />
        <div class="text-sm">
            <p class="font-medium">영수증 이미지를 끌어다 놓거나 클릭해서 선택하세요</p>
            <p class="mt-1 text-muted-foreground">jpg / png / webp, 10MB 이하</p>
        </div>
        <input
            ref="inputEl"
            type="file"
            accept="image/jpeg,image/png,image/webp"
            class="hidden"
            @change="onPicked"
        />
    </div>
</template>

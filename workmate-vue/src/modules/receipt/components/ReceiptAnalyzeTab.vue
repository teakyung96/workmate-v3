<script setup lang="ts">
/**
 * 영수증 [분석] 탭 — 업로드 → (미리보기) → 분석 → 결과 확인 폼 → 저장.
 * 상태·동작은 useReceiptAnalyze 컴포저블이 보유하고, 여기선 화면 전환만 담당한다.
 */
import { computed } from 'vue'
import { CheckCircle2, RotateCcw } from 'lucide-vue-next'
import { Button } from '@/common/components/ui/button'
import { Badge } from '@/common/components/ui/badge'
import { Spinner } from '@/common/components/ui/spinner'
import { Alert, AlertDescription } from '@/common/components/ui/alert'
import { useReceiptAnalyze } from '../composables/useReceiptAnalyze'
import ReceiptUpload from './ReceiptUpload.vue'
import CopyField from './CopyField.vue'

const emit = defineEmits<{ saved: [] }>()

const {
    previewUrl,
    analyzing,
    saving,
    saved,
    error,
    analysis,
    form,
    canSave,
    selectFile,
    analyze,
    save,
    reset,
} = useReceiptAnalyze()

// 금액(number|null)을 CopyField(string)와 잇는 양방향 브리지 — 숫자만 남겨 저장값에 반영
const amountStr = computed<string>({
    get: () => (form.payAmount === null ? '' : String(form.payAmount)),
    set: (v) => {
        const digits = v.replace(/[^\d]/g, '')
        form.payAmount = digits === '' ? null : Number(digits)
    },
})

// 사업자번호 체크섬 실패 여부 (분석 결과 기준)
const bizNoInvalid = computed(() => analysis.value?.bizNoValid === false)

async function onSave(): Promise<void> {
    const ok = await save()
    if (ok) emit('saved')
}
</script>

<template>
    <div class="flex flex-col gap-4">
        <Alert v-if="error" variant="destructive">
            <AlertDescription>{{ error }}</AlertDescription>
        </Alert>

        <!-- 1) 업로드 전 -->
        <ReceiptUpload v-if="!previewUrl" @select="selectFile" />

        <!-- 2) 이미지 선택됨 · 분석 결과 -->
        <div v-else class="flex flex-col gap-4 md:flex-row md:items-start">
            <!-- 원본 이미지 미리보기 -->
            <div class="md:w-2/5">
                <img
                    :src="previewUrl"
                    alt="영수증 미리보기"
                    class="w-full rounded-lg border object-contain"
                />
            </div>

            <!-- 우측: 분석 전이면 버튼, 분석 후면 확인 폼 -->
            <div class="flex flex-col gap-4 md:flex-1">
                <!-- 분석 전 -->
                <template v-if="!analysis">
                    <div class="flex gap-2">
                        <Button :disabled="analyzing" @click="analyze">
                            <Spinner v-if="analyzing" class="mr-2 size-4" />
                            {{ analyzing ? '분석 중…' : '분석하기' }}
                        </Button>
                        <Button variant="outline" :disabled="analyzing" @click="reset">
                            다시 선택
                        </Button>
                    </div>
                    <p class="text-sm text-muted-foreground">
                        분석하면 결과가 이력에 자동 저장되며, 아래에서 값을 확인·수정할 수 있습니다.
                    </p>
                </template>

                <!-- 분석 후: 확인 폼 -->
                <template v-else>
                    <div class="flex items-center gap-2">
                        <Badge v-if="analysis.selectType === 'AUTO'" variant="default">
                            ✅ 카드 자동 선택
                        </Badge>
                        <Badge v-else variant="secondary">✋ 수동 선택 필요</Badge>
                        <span v-if="analysis.cardName" class="text-sm text-muted-foreground">
                            {{ analysis.cardName }}
                        </span>
                    </div>

                    <CopyField v-model="amountStr" label="금액" placeholder="숫자만" numeric />
                    <CopyField
                        v-model="form.bizNo"
                        label="사업자등록번호"
                        placeholder="하이픈 없는 10자리"
                        numeric
                    >
                        <template #hint>
                            <p v-if="bizNoInvalid" class="text-xs text-destructive">
                                ⚠ 체크섬 검증 실패 — 번호를 확인해 주세요.
                            </p>
                        </template>
                    </CopyField>
                    <CopyField
                        v-model="form.payDate"
                        label="결제일"
                        placeholder="YYYYMMDD"
                        numeric
                    />
                    <CopyField
                        v-model="form.cardName"
                        label="카드사"
                        placeholder="예: 롯데법인카드"
                    />

                    <div class="flex items-center gap-2">
                        <Button :disabled="!canSave" @click="onSave">
                            <Spinner v-if="saving" class="mr-2 size-4" />
                            {{ saving ? '저장 중…' : '저장' }}
                        </Button>
                        <Button variant="outline" @click="reset">
                            <RotateCcw class="mr-2 size-4" />
                            다른 영수증
                        </Button>
                        <span v-if="saved" class="flex items-center gap-1 text-sm text-green-600">
                            <CheckCircle2 class="size-4" /> 저장됨
                        </span>
                    </div>
                </template>
            </div>
        </div>
    </div>
</template>

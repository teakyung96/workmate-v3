import { ref } from 'vue'
import { receiptApi } from '../api/receipt.api'
import { extractErrorMessage } from '@/common/utils/error'
import type { Receipt } from '../types'

/**
 * 영수증 [이력] 탭 상태·동작.
 * 이력 목록 로드와 CSV 다운로드를 담당한다.
 *
 * @returns 이력 상태와 액션들
 */
export function useReceiptHistory() {
    const receipts = ref<Receipt[]>([])
    const loading = ref(false)
    const error = ref<string | null>(null)

    /** 이력 목록 로드 (최신순) */
    async function load(): Promise<void> {
        loading.value = true
        error.value = null
        try {
            receipts.value = await receiptApi.history()
        } catch (e) {
            error.value = extractErrorMessage(e, '영수증 이력을 불러오지 못했습니다.')
        } finally {
            loading.value = false
        }
    }

    /** CSV 다운로드 — blob을 받아 임시 링크로 저장 트리거 */
    async function downloadCsv(): Promise<void> {
        try {
            const blob = await receiptApi.downloadCsv()
            const url = URL.createObjectURL(blob)
            const a = document.createElement('a')
            a.href = url
            a.download = 'receipts.csv'
            a.click()
            URL.revokeObjectURL(url)
        } catch (e) {
            error.value = extractErrorMessage(e, 'CSV 다운로드에 실패했습니다.')
        }
    }

    return { receipts, loading, error, load, downloadCsv }
}

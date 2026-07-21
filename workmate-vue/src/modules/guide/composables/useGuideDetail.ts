import { ref } from 'vue'
import { extractErrorMessage } from '@/common/utils/error'
import { guideApi } from '../api/guide.api'
import type { Guide } from '../types'

/**
 * 가이드 상세 화면 로직 (≈ Service). 단건 조회와 삭제를 담당한다.
 */
export function useGuideDetail() {
    const guide = ref<Guide | null>(null)
    const loading = ref(false)
    const error = ref('')

    /** 상세 조회 */
    async function load(guideSeq: number): Promise<void> {
        loading.value = true
        error.value = ''
        try {
            guide.value = await guideApi.detail(guideSeq)
        } catch (e) {
            error.value = extractErrorMessage(e, '가이드 문서를 불러오지 못했습니다.')
        } finally {
            loading.value = false
        }
    }

    /** 삭제 */
    async function remove(guideSeq: number): Promise<void> {
        await guideApi.remove(guideSeq)
    }

    return { guide, loading, error, load, remove }
}

import { ref } from 'vue'
import { extractErrorMessage } from '@/common/utils/error'
import { guideApi } from '../api/guide.api'
import type { Guide } from '../types'

/**
 * 가이드 목록 화면 로직 (≈ Service). 목록 로드와 삭제를 담당한다.
 */
export function useGuideList() {
    const guides = ref<Guide[]>([])
    const loading = ref(false)
    const error = ref('')

    /** 목록 조회 */
    async function load(): Promise<void> {
        loading.value = true
        error.value = ''
        try {
            guides.value = await guideApi.list()
        } catch (e) {
            error.value = extractErrorMessage(e, '가이드 목록을 불러오지 못했습니다.')
        } finally {
            loading.value = false
        }
    }

    /** 삭제 후 목록에서 제거 (낙관적 갱신) */
    async function remove(guideSeq: number): Promise<void> {
        await guideApi.remove(guideSeq)
        guides.value = guides.value.filter((g) => g.guideSeq !== guideSeq)
    }

    return { guides, loading, error, load, remove }
}

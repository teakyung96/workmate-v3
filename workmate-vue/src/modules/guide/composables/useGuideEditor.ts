import { reactive, ref } from 'vue'
import { extractErrorMessage } from '@/common/utils/error'
import { guideApi } from '../api/guide.api'
import type { Guide, GuideSaveRequest } from '../types'

/**
 * 가이드 작성·수정 폼 로직 (≈ Service). 신규(create)와 수정(update)을 함께 처리한다.
 */
export function useGuideEditor() {
    const form = reactive<GuideSaveRequest>({
        title: '',
        content: '',
        isPublic: true,
    })
    const loading = ref(false)
    const error = ref('')

    /** 수정 모드 진입 시 기존 문서 값을 폼에 채운다 */
    async function loadForEdit(guideSeq: number): Promise<void> {
        loading.value = true
        error.value = ''
        try {
            const guide = await guideApi.detail(guideSeq)
            form.title = guide.title
            form.content = guide.content
            form.isPublic = guide.isPublic
        } catch (e) {
            error.value = extractErrorMessage(e, '가이드 문서를 불러오지 못했습니다.')
        } finally {
            loading.value = false
        }
    }

    /**
     * 저장. guideSeq가 null이면 신규 등록, 아니면 수정.
     * @returns 저장된 문서 (실패 시 null, error에 사유)
     */
    async function submit(guideSeq: number | null): Promise<Guide | null> {
        loading.value = true
        error.value = ''
        try {
            const payload: GuideSaveRequest = { ...form }
            return guideSeq === null
                ? await guideApi.create(payload)
                : await guideApi.update(guideSeq, payload)
        } catch (e) {
            error.value = extractErrorMessage(e, '가이드 저장에 실패했습니다.')
            return null
        } finally {
            loading.value = false
        }
    }

    return { form, loading, error, loadForEdit, submit }
}

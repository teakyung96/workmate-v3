/** 가이드 문서 (WAS GuideResponseVo와 대응) */
export interface Guide {
    guideSeq: number
    userSeq: number
    title: string
    /** 본문 (마크다운 형식) */
    content: string
    /** 공개 여부 (true: 공개, false: 비공개) */
    isPublic: boolean
    createdAt: string
    updatedAt: string
}

/** 가이드 작성·수정 요청 (WAS GuideSaveRequestVo와 대응) */
export interface GuideSaveRequest {
    title: string
    content: string
    isPublic: boolean
}

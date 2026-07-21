package com.workmate.was.guide.service;

import com.workmate.was.guide.vo.GuideResponseVo;
import com.workmate.was.guide.vo.GuideSaveRequestVo;
import java.util.List;

/**
 * 가이드 문서 처리 및 벡터 스토어 RAG 적재 서비스 인터페이스 (F4).
 * 수정·삭제는 본인 문서만, 조회는 본인 또는 공개 문서만 허용한다 (F4-01·08).
 */
public interface GuideService {

    /** 가이드 등록 (+임베딩, G3) */
    GuideResponseVo createGuide(Long userSeq, GuideSaveRequestVo request);

    /** 가이드 수정 (+재임베딩, G4) — 본인 문서만 */
    GuideResponseVo updateGuide(Long userSeq, Long guideSeq, GuideSaveRequestVo request);

    /** 가이드 삭제 (+청크 삭제, G5) — 본인 문서만 */
    void deleteGuide(Long userSeq, Long guideSeq);

    /** 가이드 상세 (G2) — 본인 또는 공개 문서만 */
    GuideResponseVo getGuide(Long userSeq, Long guideSeq);

    /** 접근 가능 목록 (G1) — 본인 문서 + 공개 문서, 최신순 */
    List<GuideResponseVo> getAccessibleGuides(Long userSeq);
}

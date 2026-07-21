package com.workmate.was.guide.vo;

/**
 * RAG 검색으로 찾은 가이드 청크 (출처 표시·프롬프트 주입용, F4-06·07).
 *
 * @param guideSeq 출처 문서 식별자 (출처 클릭 시 이동)
 * @param title    출처 문서 제목
 * @param content  청크 본문
 */
public record GuideSourceChunk(Long guideSeq, String title, String content) {
}

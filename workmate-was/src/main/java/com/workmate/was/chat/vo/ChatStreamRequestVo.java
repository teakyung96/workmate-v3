package com.workmate.was.chat.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 채팅 메시지 전송(스트리밍) 요청 VO (C3, 04 §3.2).
 * roomSeq 가 null 이면 첫 메시지 전송으로 간주해 방을 생성한다 (F2-02).
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatStreamRequestVo {
    /** 대상 채팅방 — null 이면 신규 생성 */
    private Long roomSeq;
    /** 사용자 질문 */
    private String message;
    /** 사용할 모델 코드 (AI_MODEL 공통코드 값). null 이면 기본 모델 */
    private String modelCode;
    /** 첨부 이미지 (선택) — 있으면 AI 비전 입력으로 전달 */
    private ChatImageVo image;
    /** RAG 모드 — true 면 가이드 문서에서 유사 청크를 검색해 프롬프트에 포함 (F4-05) */
    private boolean ragMode;
}

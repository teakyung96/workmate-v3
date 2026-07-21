package com.workmate.was.chat.service.impl;

import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
 * 스트리밍 시작 전 동기 준비 결과 (방 확보 + 사용자 메시지 저장 + 맥락 로드).
 *
 * @param roomSeq 확보된 방 식별자
 * @param title   방 제목 (신규 생성 시 meta 이벤트로 전달)
 * @param isNew   이번 요청에서 방이 새로 생성됐는지 (F2-02)
 * @param history 이전 대화 맥락 (시간순, Spring AI Message)
 */
record PreparedChat(Long roomSeq, String title, boolean isNew, List<Message> history) {
}

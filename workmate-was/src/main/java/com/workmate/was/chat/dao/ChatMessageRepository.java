package com.workmate.was.chat.dao;

import com.workmate.was.chat.vo.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 채팅 메시지 조회/저장 Repository.
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /** 방 대화 이력 시간순 (F2-08) */
    List<ChatMessage> findByRoomSeqOrderByCreatedAtAsc(Long roomSeq);

    /** 맥락 포함용 최근 N개 (F2-10) — 최신순으로 뽑고 서비스에서 시간순 재정렬. Pageable 로 개수 제한 */
    List<ChatMessage> findByRoomSeqOrderByCreatedAtDesc(Long roomSeq, Pageable pageable);
}

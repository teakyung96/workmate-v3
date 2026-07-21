package com.workmate.was.chat.dao;

import com.workmate.was.chat.vo.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 채팅방 조회/저장 Repository.
 * 목록·소유권 검증 모두 use_yn=true(미삭제) 기준으로 조회한다.
 */
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /** 본인 채팅방 최신순 목록 (F2-03) */
    List<ChatRoom> findByUserSeqAndUseYnTrueOrderByCreatedAtDesc(Long userSeq);

    /** 소유권 검증용 — 본인의 미삭제 방만 반환 */
    Optional<ChatRoom> findByRoomSeqAndUserSeqAndUseYnTrue(Long roomSeq, Long userSeq);
}

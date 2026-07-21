package com.workmate.was.guide.dao;

import com.workmate.was.guide.vo.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 가이드 문서 엔티티에 대한 JPA 리포지토리 인터페이스.
 */
public interface GuideRepository extends JpaRepository<Guide, Long> {

    /**
     * 특정 작성자가 등록한 가이드 문서 리스트를 최신순으로 조회한다.
     *
     * @param userSeq 사용자 식별자
     * @return 가이드 문서 목록
     */
    List<Guide> findByUserSeqOrderByCreatedAtDesc(Long userSeq);

    /**
     * 공개 상태인 가이드 문서 리스트를 최신순으로 조회한다.
     *
     * @param isPublic 공개 여부 (true)
     * @return 공개 가이드 문서 목록
     */
    List<Guide> findByIsPublicOrderByCreatedAtDesc(boolean isPublic);
}

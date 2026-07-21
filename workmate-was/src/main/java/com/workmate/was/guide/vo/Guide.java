package com.workmate.was.guide.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 가이드 문서 정보 엔티티.
 * 사용자가 작성한 지식 가이드 문서 내용을 보관하며, RAG 검색의 근거 자료가 된다.
 */
@Entity
@Table(name = "guide")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Guide {

    /** 가이드 문서 식별자 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_seq")
    private Long guideSeq;

    /** 작성자(사용자) 식별자 (FK) */
    @Column(name = "user_seq", nullable = false)
    private Long userSeq;

    /** 문서 제목 */
    @Column(name = "title", nullable = false)
    private String title;

    /** 문서 본문 (마크다운 형식) */
    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    /** 공개 여부 (true: 공개, false: 비공개) */
    @Builder.Default
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    /** 최초 생성 일시 */
    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /** 최종 수정 일시 */
    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * 가이드 문서의 제목, 내용, 공개 여부를 수정한다.
     *
     * @param title 수정할 제목
     * @param content 수정할 본문
     * @param isPublic 수정할 공개 여부
     */
    public void update(String title, String content, Boolean isPublic) {
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
        this.updatedAt = LocalDateTime.now();
    }
}

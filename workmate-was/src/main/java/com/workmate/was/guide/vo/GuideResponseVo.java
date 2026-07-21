package com.workmate.was.guide.vo;

import java.time.LocalDateTime;
import lombok.Getter;

/** 가이드 문서 정보 응답 VO. */
@Getter
public class GuideResponseVo {

    private final Long guideSeq;
    private final Long userSeq;
    private final String title;
    private final String content;
    private final Boolean isPublic;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public GuideResponseVo(Guide guide) {
        this.guideSeq = guide.getGuideSeq();
        this.userSeq = guide.getUserSeq();
        this.title = guide.getTitle();
        this.content = guide.getContent();
        this.isPublic = guide.getIsPublic();
        this.createdAt = guide.getCreatedAt();
        this.updatedAt = guide.getUpdatedAt();
    }
}

package com.workmate.was.guide.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 가이드 문서 작성 및 수정을 요청하는 VO.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuideSaveRequestVo {

    /** 문서 제목 */
    @NotBlank(message = "가이드 문서 제목은 필수 입력 항목입니다.")
    private String title;

    /** 문서 본문 (마크다운 형식) */
    @NotBlank(message = "가이드 문서 본문은 필수 입력 항목입니다.")
    private String content;

    /** 공개 여부 (true: 공개, false: 비공개) */
    @NotNull(message = "공개 여부 설정은 필수입니다.")
    private Boolean isPublic;
}

package com.workmate.was.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 관리자 사용자 목록 페이지 응답 VO (M1) — 목록 + 페이징 메타.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPageVo {
    private List<AdminUserVo> content;
    /** 0-based 현재 페이지 */
    private int page;
    private int totalPages;
    private long totalElements;
}

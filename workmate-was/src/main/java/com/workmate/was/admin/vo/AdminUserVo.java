package com.workmate.was.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 관리자 사용자 목록 항목 VO (M1). 이메일·전화번호는 WAS 에서 마스킹된 값만 담는다 (§3.5, F6-01).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserVo {
    private Long userSeq;
    private String maskedEmail;
    private String userName;
    private String maskedPhone;
    private String role;
    /** 현재 잠금 상태 여부 (F6-02) */
    private boolean locked;
    private LocalDateTime createdAt;
}

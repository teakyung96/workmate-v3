package com.workmate.was.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 초기화 결과 VO (M3) — 임시 비밀번호 평문. 화면에 1회만 표시하고 재조회 불가 (F6-03).
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordResultVo {
    private String tempPassword;
}

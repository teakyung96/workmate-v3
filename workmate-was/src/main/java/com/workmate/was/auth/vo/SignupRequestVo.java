package com.workmate.was.auth.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 회원가입 요청 DTO (A1). 정규화·정책 검증은 서비스에서 수행 — DTO 는 운반만 */
@Getter
@Setter
@NoArgsConstructor
public class SignupRequestVo {
    private String email;
    private String password;
    private String userName;
    private String phone;
}

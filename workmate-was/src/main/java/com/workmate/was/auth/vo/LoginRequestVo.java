package com.workmate.was.auth.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 로그인(자격 검증) 요청 DTO (A2) */
@Getter
@Setter
@NoArgsConstructor
public class LoginRequestVo {
    private String email;
    private String password;
}

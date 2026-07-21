package com.workmate.web.auth.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 회원가입 폼 바인딩 VO — 검증은 WAS 가 담당, WEB 은 운반만 (F9 대원칙) */
@Getter
@Setter
@NoArgsConstructor
public class SignupForm {
    private String email;
    private String password;
    private String userName;
    private String phone;
}

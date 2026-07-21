package com.workmate.was.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 로그인 성공 응답 — WEB 이 세션 principal 로 사용한다 (04 문서 §3.1) */
@Getter
@AllArgsConstructor
public class LoginResponseVo {
    private Long userSeq;
    private String userName;
    private String role;
}

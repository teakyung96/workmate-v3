package com.workmate.web.global.security;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

/**
 * 세션에 보관되는 로그인 사용자 정보 (Authentication principal).
 * WAS 프록시 호출 시 X-User-Seq 헤더의 원천이다 (04 문서 §1.4).
 *
 * <p>equals/hashCode 를 userSeq 로 값 동등화한다 — SessionRegistry 가 동일 사용자의
 * 재로그인을 인식해 maximumSessions(1) 로 기존 세션을 축출하려면 principal 값 동등성이 필요하다 (F1-08).</p>
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "userSeq")
public class LoginUser implements Serializable {

    private final Long userSeq;
    private final String userName;
    private final String role;
}

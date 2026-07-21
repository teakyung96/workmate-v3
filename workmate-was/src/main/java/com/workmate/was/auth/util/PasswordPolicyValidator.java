package com.workmate.was.auth.util;

import java.util.regex.Pattern;

/**
 * 비밀번호 정책 검증 (F1-04): 8자 이상 + 영문 + 숫자 + 특수문자 조합.
 * 서버 측 검증이 방어선이다 — 클라이언트 검증과 무관하게 항상 수행 (F9 대원칙).
 */
public final class PasswordPolicyValidator {

    private static final Pattern POLICY =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d\\s]).{8,}$");

    private PasswordPolicyValidator() {
    }

    /** @throws IllegalArgumentException 정책 위반 시 */
    public static void validate(String password) {
        if (password == null || !POLICY.matcher(password).matches()) {
            throw new IllegalArgumentException("비밀번호는 8자 이상, 영문·숫자·특수문자를 모두 포함해야 합니다.");
        }
    }
}

package com.workmate.was.auth.util;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 이메일 정규화 유틸 (F1-01a·F9-01).
 * WAS 진입 시점에 trim + 소문자 변환을 강제해, 클라이언트 검증을 우회한 입력도
 * 항상 정규형으로 중복검사·암호화·저장·로그인 대조가 이뤄지게 한다.
 */
public final class EmailNormalizer {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private EmailNormalizer() {
    }

    /**
     * @param raw 사용자 입력 이메일
     * @return trim + 소문자 정규형
     * @throws IllegalArgumentException null·빈값·형식 위반
     */
    public static String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("이메일을 입력해주세요.");
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if (!EMAIL.matcher(normalized).matches()) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }
        return normalized;
    }
}

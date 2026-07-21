package com.workmate.was.auth.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailNormalizerTest {

    @Test
    void 대문자와_공백을_정규화한다() {
        // 클라이언트 검증을 우회한 입력도 서버가 정규화 (F1-01a — 기존 프로젝트 버그 재발 방지)
        assertThat(EmailNormalizer.normalize("  User@Example.COM ")).isEqualTo("user@example.com");
    }

    @Test
    void 이미_정규형이면_그대로다() {
        assertThat(EmailNormalizer.normalize("user@example.com")).isEqualTo("user@example.com");
    }

    @Test
    void null_이나_빈_문자열은_거부한다() {
        assertThatThrownBy(() -> EmailNormalizer.normalize(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> EmailNormalizer.normalize("   ")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 이메일_형식이_아니면_거부한다() {
        assertThatThrownBy(() -> EmailNormalizer.normalize("not-an-email")).isInstanceOf(IllegalArgumentException.class);
    }
}

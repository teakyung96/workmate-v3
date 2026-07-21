package com.workmate.was.global.crypto;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class AesCipherTest {

    // 테스트 전용 고정 키/IV (32바이트·16바이트를 Base64 인코딩한 값)
    private static final String KEY = Base64.getEncoder().encodeToString("01234567890123456789012345678901".getBytes());
    private static final String IV = Base64.getEncoder().encodeToString("0123456789012345".getBytes());

    private final AesCipher cipher = new AesCipher(KEY, IV);

    @Test
    void 암호화_후_복호화하면_원문이_돌아온다() {
        String encrypted = cipher.encrypt("user@example.com");
        assertThat(encrypted).isNotEqualTo("user@example.com");
        assertThat(cipher.decrypt(encrypted)).isEqualTo("user@example.com");
    }

    @Test
    void 같은_평문은_항상_같은_암호문이다_결정적_암호화() {
        // email UNIQUE 제약·검색이 동작하려면 결정적이어야 한다 (04 문서 §4.1)
        assertThat(cipher.encrypt("user@example.com")).isEqualTo(cipher.encrypt("user@example.com"));
    }

    @Test
    void null_은_null_로_통과시킨다() {
        assertThat(cipher.encrypt(null)).isNull();
        assertThat(cipher.decrypt(null)).isNull();
    }
}

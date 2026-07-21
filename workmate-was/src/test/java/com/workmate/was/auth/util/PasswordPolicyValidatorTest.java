package com.workmate.was.auth.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordPolicyValidatorTest {

    @Test
    void 정책을_만족하면_통과한다() {
        assertThatCode(() -> PasswordPolicyValidator.validate("abcd123!")).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ab12!",        // 8자 미만
            "abcdefgh!",    // 숫자 없음
            "12345678!",    // 영문 없음
            "abcd1234",     // 특수문자 없음
    })
    void 정책_위반은_거부한다(String password) {
        assertThatThrownBy(() -> PasswordPolicyValidator.validate(password))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

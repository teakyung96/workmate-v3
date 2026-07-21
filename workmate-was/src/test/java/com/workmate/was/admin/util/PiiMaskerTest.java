package com.workmate.was.admin.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PiiMasker 단위 테스트 (F6-01 마스킹).
 */
class PiiMaskerTest {

    @Test
    @DisplayName("이메일: 로컬 첫 글자·도메인 첫 글자·TLD 만 남기고 마스킹")
    void maskEmail_masks() {
        assertThat(PiiMasker.maskEmail("kim@gmail.com")).isEqualTo("k**@g***.com");
        assertThat(PiiMasker.maskEmail("a@b.co")).isEqualTo("a**@b***.co");
    }

    @Test
    @DisplayName("이메일: null·형식 이상은 원문 그대로")
    void maskEmail_passthrough_invalid() {
        assertThat(PiiMasker.maskEmail(null)).isNull();
        assertThat(PiiMasker.maskEmail("notanemail")).isEqualTo("notanemail");
    }

    @Test
    @DisplayName("전화번호: 앞 3·뒤 4 유지, 가운데 마스킹")
    void maskPhone_masks() {
        assertThat(PiiMasker.maskPhone("01012345678")).isEqualTo("010****5678");
        assertThat(PiiMasker.maskPhone("010-1234-5678")).isEqualTo("010****5678");
    }

    @Test
    @DisplayName("전화번호: null·빈값은 그대로")
    void maskPhone_passthrough_blank() {
        assertThat(PiiMasker.maskPhone(null)).isNull();
        assertThat(PiiMasker.maskPhone("")).isEqualTo("");
    }
}

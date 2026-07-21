package com.workmate.was.global.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES-256/CBC 양방향 암복호화 유틸.
 * 고정 IV 를 사용하는 결정적(deterministic) 암호화 — 같은 평문은 항상 같은 암호문을 만든다.
 * (email 컬럼의 UNIQUE 제약과 동등 검색을 위해 필요. 무작위 IV 대비 보안 트레이드오프는 04 문서 §4.1 참고)
 */
public class AesCipher {

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private final SecretKeySpec key;
    private final IvParameterSpec iv;

    /**
     * @param base64Key Base64 인코딩된 32바이트 키
     * @param base64Iv  Base64 인코딩된 16바이트 고정 IV
     */
    public AesCipher(String base64Key, String base64Iv) {
        this.key = new SecretKeySpec(Base64.getDecoder().decode(base64Key), "AES");
        this.iv = new IvParameterSpec(Base64.getDecoder().decode(base64Iv));
    }

    /** 평문 → Base64 암호문 (null 은 null 통과) */
    public String encrypt(String plain) {
        if (plain == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            return Base64.getEncoder().encodeToString(cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("AES 암호화 실패", e);
        }
    }

    /** Base64 암호문 → 평문 (null 은 null 통과) */
    public String decrypt(String encrypted) {
        if (encrypted == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("AES 복호화 실패", e);
        }
    }
}

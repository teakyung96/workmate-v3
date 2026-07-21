package com.workmate.was.global.crypto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AES 암호화 키 프로퍼티 바인딩 (application.yml 의 app.crypto.*).
 */
@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "app.crypto")
public class CryptoProperties {

    /** Base64 인코딩된 32바이트 AES 키 */
    private final String aesKey;

    /** Base64 인코딩된 16바이트 고정 IV */
    private final String aesIv;
}

package com.workmate.was.global.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 개인정보 컬럼(이메일·전화번호) 투명 암호화 JPA 컨버터.
 * Entity 필드에 @Convert(converter = AesCryptoConverter.class) 로 지정하면
 * 저장 시 암호화, 조회 시 복호화가 자동 수행된다 (F1-03).
 * Spring Boot 의 SpringBeanContainer 가 이 @Component 를 Hibernate 에 주입한다.
 */
@Component
@Converter
@EnableConfigurationProperties(CryptoProperties.class)
public class AesCryptoConverter implements AttributeConverter<String, String> {

    private final AesCipher cipher;

    public AesCryptoConverter(CryptoProperties properties) {
        this.cipher = new AesCipher(properties.getAesKey(), properties.getAesIv());
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return cipher.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return cipher.decrypt(dbData);
    }
}

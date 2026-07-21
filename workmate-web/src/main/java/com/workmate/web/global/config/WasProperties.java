package com.workmate.web.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * WAS 레이어 접속 정보 바인딩 프로퍼티.
 * application-{profile}.yml 의 was.* 값을 타입 세이프하게 읽는다.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "was")
public class WasProperties {

    /** WAS REST API 베이스 URL (예: http://localhost:8081) */
    private String baseUrl;
}

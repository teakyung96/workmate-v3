package com.workmate.web.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WEB → WAS 스트리밍 전용 WebClient 설정.
 * RestClient 는 응답 바디를 버퍼링하므로 SSE 무버퍼 relay(F2-06)에는 WebClient 를 쓴다.
 * 블로킹 프록시(영수증·방 목록 등)는 계속 RestClient 를 사용한다.
 *
 * <p>인증 헤더(X-User-Seq)는 이 빈에 필터로 넣지 않는다 — WebClient 교환은 리액터 스레드에서
 * 일어나 SecurityContextHolder(ThreadLocal)가 비어 있을 수 있다. 대신 컨트롤러가 요청 스레드에서
 * LoginUser 를 읽어 서비스로 넘기고, 서비스가 매 요청 헤더에 직접 주입한다.</p>
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient wasWebClient(WasProperties wasProperties) {
        return WebClient.builder()
                .baseUrl(wasProperties.getBaseUrl())
                .build();
    }
}

package com.workmate.web.chat.service.impl;

import com.workmate.web.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 * 채팅 도메인 WEB 프록시 서비스 구현체.
 * 조회·삭제는 RestClient(블로킹, 인터셉터가 X-User-Seq 자동 주입), 스트리밍은 WebClient(무버퍼 relay).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final RestClient wasRestClient;
    private final WebClient wasWebClient;

    private static final ParameterizedTypeReference<ServerSentEvent<String>> SSE_TYPE =
            new ParameterizedTypeReference<>() {};

    @Override
    public ResponseEntity<String> getRooms() {
        return wasRestClient.get()
                .uri("/api/v1/chat/rooms")
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public ResponseEntity<String> getMessages(Long roomSeq) {
        return wasRestClient.get()
                .uri("/api/v1/chat/rooms/{roomSeq}/messages", roomSeq)
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public ResponseEntity<String> deleteRoom(Long roomSeq) {
        return wasRestClient.post()
                .uri("/api/v1/chat/rooms/{roomSeq}/delete", roomSeq)
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public Flux<ServerSentEvent<String>> stream(Long userSeq, String role, String requestBody) {
        log.info("채팅 스트리밍 relay - userSeq: {}", userSeq);
        return wasWebClient.post()
                .uri("/api/v1/chat/stream")
                .header("X-User-Seq", String.valueOf(userSeq))
                .header("X-User-Role", role)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(requestBody)
                // 스트림 시작 전 WAS 오류(빈 메시지 400·요청제한 429 등)는 event-stream 이 아니므로
                // 상태코드를 검사해 error 이벤트 하나로 변환한다. 정상(2xx)은 이벤트를 그대로 통과.
                .exchangeToFlux(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToFlux(SSE_TYPE);
                    }
                    return response.bodyToMono(String.class)
                            .defaultIfEmpty("{\"message\":\"요청 처리에 실패했습니다.\"}")
                            .flatMapMany(body -> Flux.just(ServerSentEvent.<String>builder()
                                    .event("error")
                                    .data(body)
                                    .build()));
                });
    }
}

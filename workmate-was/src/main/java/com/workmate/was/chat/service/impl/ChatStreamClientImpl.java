package com.workmate.was.chat.service.impl;

import com.workmate.was.chat.service.ChatStreamClient;
import com.workmate.was.guide.tool.GuideTools;
import com.workmate.was.receipt.tool.ReceiptTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Spring AI(Gemini) 기반 스트리밍 호출 구현체.
 * ChatClient.stream().content() 로 응답을 토큰 Flux 로 흘린다 (F2-05·06).
 * 영수증·가이드 조회 @Tool 을 등록하고, userSeq 를 ToolContext 로 넘겨 본인 데이터만 조회하게 한다 (F5).
 */
@Slf4j
@Component
public class ChatStreamClientImpl implements ChatStreamClient {

    private final ChatClient chatClient;
    private final ReceiptTools receiptTools;
    private final GuideTools guideTools;

    public ChatStreamClientImpl(ChatClient.Builder chatClientBuilder,
                                ReceiptTools receiptTools, GuideTools guideTools) {
        this.chatClient = chatClientBuilder.build();
        this.receiptTools = receiptTools;
        this.guideTools = guideTools;
    }

    @Override
    public Flux<String> stream(Long userSeq, String model, String systemPrompt, List<Message> history,
                               String userMessage, byte[] imageData, String imageMimeType) {
        // 첫 토큰 전 실패(타임아웃·쿼터)만 1회 재시도한다 (F2.3).
        // 이미 토큰을 흘린 뒤 재시도하면 클라이언트에 중복 응답이 쌓이므로, 재시도 조건에서 제외한다.
        AtomicBoolean tokenEmitted = new AtomicBoolean(false);
        boolean hasImage = imageData != null && imageData.length > 0;
        // 요청 모델 적용 (F5-05) — 값이 있으면 이 요청에 한해 모델을 교체한다
        org.springframework.ai.chat.prompt.ChatOptions options =
                org.springframework.ai.chat.prompt.ChatOptions.builder().model(model).build();
        // Flux.defer 로 감싸 재시도 시마다 prompt·stream 을 새로 만든다 — Spring AI 의
        // stream().content() Flux 는 재구독이 불가("No StreamAdvisors available")해서다.
        return Flux.defer(() -> chatClient.prompt()
                        .options(options)
                        .system(systemPrompt)
                        .messages(history)          // 이전 맥락 (F2-10)
                        .user(userSpec -> {
                            userSpec.text(userMessage);
                            // 첨부 이미지가 있으면 멀티모달 입력으로 추가 (OcrServiceImpl 과 동일한 media 방식)
                            if (hasImage) {
                                userSpec.media(parseMimeType(imageMimeType), new ByteArrayResource(imageData));
                            }
                        })
                        // @Tool 등록 + userSeq 컨텍스트 (F5-01·02·03). AI 가 필요 시에만 호출한다.
                        .tools(receiptTools, guideTools)
                        .toolContext(Map.of("userSeq", userSeq))
                        .stream()
                        .content())
                .doOnNext(token -> tokenEmitted.set(true))
                .retryWhen(Retry.max(1).filter(ex -> !tokenEmitted.get()));
    }

    private MimeType parseMimeType(String mimeType) {
        try {
            return MimeType.valueOf(mimeType);
        } catch (Exception e) {
            return MimeTypeUtils.IMAGE_JPEG;
        }
    }
}

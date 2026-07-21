package com.workmate.was.chat.service;

import com.workmate.was.global.exception.RateLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 사용자별 분당 AI 요청 횟수 제한기 (F2-11).
 * 인메모리 고정 윈도우(분 단위) 방식 — 단일 인스턴스 개발/데모 기준. 분산 환경은 후속 과제.
 */
@Component
public class ChatRateLimiter {

    /** 분당 허용 요청 수 */
    @Value("${app.chat.rate-limit-per-minute:20}")
    private int limitPerMinute;

    private final ConcurrentMap<Long, Window> windows = new ConcurrentHashMap<>();

    /**
     * 요청 1건을 기록하고 한도 초과 시 예외를 던진다.
     *
     * @param userSeq 요청 사용자
     * @throws RateLimitExceededException 분당 한도 초과 (429)
     */
    public void check(Long userSeq) {
        long currentMinute = System.currentTimeMillis() / 60_000L;
        // compute 는 키 단위로 원자적이라 카운터 증가가 스레드 안전하다
        Window window = windows.compute(userSeq, (key, existing) -> {
            if (existing == null || existing.minute != currentMinute) {
                return new Window(currentMinute);
            }
            existing.count++;
            return existing;
        });
        if (window.count > limitPerMinute) {
            throw new RateLimitExceededException("요청이 많습니다. 잠시 후 이용해주세요.");
        }
    }

    /** 분 단위 카운팅 윈도우 */
    private static final class Window {
        private final long minute;
        private int count;

        private Window(long minute) {
            this.minute = minute;
            this.count = 1;
        }
    }
}

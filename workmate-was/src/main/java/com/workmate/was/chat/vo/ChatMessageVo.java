package com.workmate.was.chat.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 채팅 메시지 이력 응답 VO (C2). */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageVo {
    private Long messageSeq;
    private String role;
    private String content;
    private String modelName;
    private LocalDateTime createdAt;
}

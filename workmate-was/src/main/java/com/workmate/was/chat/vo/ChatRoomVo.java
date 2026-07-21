package com.workmate.was.chat.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 채팅방 목록 응답 VO (C1). */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomVo {
    private Long roomSeq;
    private String title;
    private LocalDateTime createdAt;
}

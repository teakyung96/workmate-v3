package com.workmate.was.chat.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 채팅 첨부 이미지 (멀티모달 입력). 스트리밍 요청 JSON 에 base64 로 실려 온다.
 * 이미지는 AI 비전 호출에만 사용하고 대화 이력에는 별도 저장하지 않는다(1-B 범위).
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatImageVo {
    /** 예: image/png, image/jpeg */
    private String mimeType;
    /** Base64 인코딩된 이미지 바이트 (data URI 접두어 없이 순수 base64) */
    private String dataBase64;
}

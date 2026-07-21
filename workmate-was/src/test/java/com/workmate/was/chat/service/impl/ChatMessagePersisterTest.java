package com.workmate.was.chat.service.impl;

import com.workmate.was.chat.dao.ChatMessageRepository;
import com.workmate.was.chat.dao.ChatRoomRepository;
import com.workmate.was.chat.vo.ChatMessage;
import com.workmate.was.chat.vo.ChatRoom;
import com.workmate.was.chat.vo.ChatStreamRequestVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ChatMessagePersister 단위 테스트 (방 생성·제목 30자·맥락·저장).
 */
@ExtendWith(MockitoExtension.class)
class ChatMessagePersisterTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatMessagePersister persister;

    private ChatRoom roomWithSeq(Long roomSeq, Long userSeq, String title) {
        ChatRoom r = ChatRoom.builder().userSeq(userSeq).title(title).build();
        ReflectionTestUtils.setField(r, "roomSeq", roomSeq);
        return r;
    }

    private ChatStreamRequestVo request(Long roomSeq, String message) {
        ChatStreamRequestVo req = new ChatStreamRequestVo();
        req.setRoomSeq(roomSeq);
        req.setMessage(message);
        return req;
    }

    @Test
    @DisplayName("prepare: roomSeq null 이면 방을 생성하고 제목은 첫 질문 30자, 사용자 메시지를 저장한다")
    void prepare_creates_room_and_saves_user_message() {
        ReflectionTestUtils.setField(persister, "contextSize", 10);
        String longMessage = "가나다라마바사아자차카타파하거너더러머버서어저처커터퍼허고노도로"; // 30자 초과
        when(chatRoomRepository.save(any(ChatRoom.class)))
                .thenReturn(roomWithSeq(7L, 1L, longMessage.substring(0, 30)));
        when(chatMessageRepository.findByRoomSeqOrderByCreatedAtDesc(eq(7L), any(Pageable.class)))
                .thenReturn(List.of());

        PreparedChat result = persister.prepare(1L, request(null, longMessage));

        assertThat(result.isNew()).isTrue();
        assertThat(result.roomSeq()).isEqualTo(7L);
        assertThat(result.title()).hasSize(30);

        // 사용자 메시지가 role=user 로 저장됐는지
        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo("user");
        assertThat(captor.getValue().getContent()).isEqualTo(longMessage);
    }

    @Test
    @DisplayName("prepare: 기존 방이 본인 소유가 아니면 IllegalArgumentException, 새 방 생성 안 함")
    void prepare_rejects_other_user_room() {
        when(chatRoomRepository.findByRoomSeqAndUserSeqAndUseYnTrue(5L, 99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> persister.prepare(99L, request(5L, "안녕")))
                .isInstanceOf(IllegalArgumentException.class);
        verify(chatRoomRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveAssistant: assistant 메시지를 모델명과 함께 저장하고 messageSeq 를 반환한다")
    void saveAssistant_persists_with_model() {
        ChatMessage saved = ChatMessage.builder()
                .roomSeq(7L).role(ChatMessage.ROLE_ASSISTANT).content("답변").modelName("gemini-2.5-flash").build();
        ReflectionTestUtils.setField(saved, "messageSeq", 45L);
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(saved);

        Long messageSeq = persister.saveAssistant(7L, "답변", "gemini-2.5-flash");

        assertThat(messageSeq).isEqualTo(45L);
        ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);
        verify(chatMessageRepository).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo("assistant");
        assertThat(captor.getValue().getModelName()).isEqualTo("gemini-2.5-flash");
    }
}

package com.workmate.web.chat.controller;

import com.workmate.web.receipt.controller.ReceiptViewController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * 화면 뷰 컨트롤러 라우팅 테스트 (사이드바 이관 후: / = 채팅, /receipt = 영수증).
 * `/` 는 인증 필요(F1-10) — 인증된 사용자만 200 을 받는다.
 */
@WebMvcTest({ChatViewController.class, ReceiptViewController.class})
class ChatViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("인증된 사용자의 GET / 는 chat 뷰를 렌더링한다")
    @WithMockUser
    void root_rendersChatView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("chat"));
    }

    @Test
    @DisplayName("인증된 사용자의 GET /receipt 는 receipt 뷰를 렌더링한다")
    @WithMockUser
    void receipt_rendersReceiptView() throws Exception {
        mockMvc.perform(get("/receipt"))
                .andExpect(status().isOk())
                .andExpect(view().name("receipt"));
    }
}

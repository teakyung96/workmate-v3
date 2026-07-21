package com.workmate.was.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workmate.was.auth.vo.LoginResponseVo;
import com.workmate.was.auth.service.AuthService;
import com.workmate.was.global.exception.AuthenticationFailedException;
import com.workmate.was.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthApiControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthApiController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void 로그인_성공_시_사용자_정보를_반환한다() throws Exception {
        when(authService.login(any())).thenReturn(new LoginResponseVo(1L, "김태경", "ROLE_USER"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "user@example.com", "password", "abcd123!"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result.userSeq").value(1))
                .andExpect(jsonPath("$.result.role").value("ROLE_USER"));
    }

    @Test
    void 자격_불일치는_401_공통_포맷이다() throws Exception {
        when(authService.login(any())).thenThrow(new AuthenticationFailedException("이메일 또는 비밀번호가 올바르지 않습니다."));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "user@example.com", "password", "bad"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void 회원가입_성공은_200_이다() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "user@example.com", "password", "abcd123!",
                                "userName", "김태경", "phone", "010-1234-5678"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}

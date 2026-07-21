package com.workmate.web.auth.service.impl;

import com.workmate.web.auth.service.AuthService;
import com.workmate.web.auth.vo.SignupForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * 회원가입 WAS 프록시 구현 — wasRestClient 로만 WAS 와 통신한다 (가이드 §1).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RestClient wasRestClient;

    @Override
    @SuppressWarnings("unchecked")
    public String signup(SignupForm form) {
        ResponseEntity<Map> entity = wasRestClient.post()
                .uri("/api/v1/auth/signup")
                .body(Map.of(
                        "email", form.getEmail(),
                        "password", form.getPassword(),
                        "userName", form.getUserName(),
                        "phone", form.getPhone() == null ? "" : form.getPhone()))
                .retrieve()
                .toEntity(Map.class);

        Map<String, Object> body = entity.getBody();
        boolean success = body != null && Boolean.TRUE.equals(body.get("success"));
        if (success) {
            log.info("회원가입 프록시 성공");
            return null;
        }
        String message = body != null ? String.valueOf(body.get("message")) : "가입에 실패했습니다.";
        log.warn("회원가입 프록시 실패 - {}", message);
        return message;
    }
}

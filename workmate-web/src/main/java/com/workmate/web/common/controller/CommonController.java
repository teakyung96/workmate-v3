package com.workmate.web.common.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

/**
 * 공통코드 WEB 프록시 컨트롤러 (K1, F7) — 화면(fetch)의 /api/common/codes/** 를 WAS 로 중계한다.
 */
@RestController
@RequestMapping("/api/common/codes")
@RequiredArgsConstructor
public class CommonController {

    private final RestClient wasRestClient;

    @GetMapping("/{groupCode}")
    public ResponseEntity<String> getCodes(@PathVariable("groupCode") String groupCode) {
        ResponseEntity<String> was = wasRestClient.get()
                .uri("/api/common/codes/{groupCode}", groupCode)
                .retrieve()
                .toEntity(String.class);
        return ResponseEntity.status(was.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(was.getBody());
    }
}

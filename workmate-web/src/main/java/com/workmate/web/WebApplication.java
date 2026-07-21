package com.workmate.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Workmate WEB 레이어 진입점.
 * Thymeleaf 화면 렌더링·세션 인증·WAS 프록시를 담당한다. (포트 8080)
 */
@SpringBootApplication
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}

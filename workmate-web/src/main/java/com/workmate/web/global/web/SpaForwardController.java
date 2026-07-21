package com.workmate.web.global.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SPA 딥링크 fallback — 매핑되지 않은 경로를 index.html로 forward한다.
 *
 * <p>SPA는 클라이언트 라우팅이라 /chat·/guide 같은 경로를 브라우저에서 직접 새로고침하면
 * 서버엔 해당 자원이 없어 404가 난다. 이를 index.html로 넘겨 Vue Router가 처리하게 한다.</p>
 *
 * <p>{@code /{*path}} 는 Spring PathPattern의 "나머지 경로 전체" 캡처(0개 이상 세그먼트)다.
 * /api/** 는 각 REST 컨트롤러가 더 구체적으로 매핑되어 우선하고, 확장자 있는 정적 자원(.js·.css)은
 * 정적 리소스 핸들러가 처리한다. (정적 자원 서빙·index.html 배치는 빌드 연결 4단계에서 확정)</p>
 */
@Controller
public class SpaForwardController {

    @RequestMapping(value = {"/", "/{*path}"})
    public String forward() {
        return "forward:/index.html";
    }
}

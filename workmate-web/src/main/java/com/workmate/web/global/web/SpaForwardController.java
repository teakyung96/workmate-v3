package com.workmate.web.global.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SPA 딥링크 fallback — 확장자 없는(=정적 파일이 아닌) 경로를 index.html로 forward한다.
 *
 * <p>SPA는 클라이언트 라우팅이라 /chat·/guide 같은 경로를 브라우저에서 직접 새로고침하면
 * 서버엔 해당 자원이 없어 404가 난다. 이를 index.html로 넘겨 Vue Router가 처리하게 한다.
 * `{path:[^\.]*}` 패턴은 .js·.css 같은 정적 파일(확장자 포함)은 제외하므로 자원 서빙과 충돌하지 않고,
 * /api/** 는 각 REST 컨트롤러가 더 구체적으로 매핑되어 우선한다.</p>
 *
 * <p>실제 index.html은 빌드 연결(HANDOVER 4단계)에서 workmate-vue 산출물이 채운다.</p>
 */
@Controller
public class SpaForwardController {

    @RequestMapping(value = {"/", "/{path:[^\\.]*}", "/{path:^(?!api$).*}/**/{sub:[^\\.]*}"})
    public String forward() {
        return "forward:/index.html";
    }
}

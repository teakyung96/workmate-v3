package com.workmate.web.global.web;

import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * SPA 정적 서빙 + 딥링크 fallback 설정.
 *
 * <p>운영에서는 workmate-vue 빌드 산출물(dist)이 {@code classpath:/static/} 에 놓인다.
 * 브라우저 요청을 이렇게 처리한다:
 * <ul>
 *   <li>실제 정적 파일(index.html·assets/*.js·*.css 등)이 있으면 그대로 서빙</li>
 *   <li>파일이 없는 경로(예: /chat·/guide/3 새로고침)는 index.html로 fallback → Vue Router가 처리</li>
 *   <li>/api/** 는 REST 컨트롤러가 더 구체적으로 매핑되어 우선하고, 여기선 제외</li>
 * </ul>
 * (앞선 catch-all 컨트롤러 방식은 .js·.css 정적 파일까지 삼키는 문제가 있어 이 방식으로 대체했다.)
 */
@Configuration
public class SpaWebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        // /api 요청은 정적 서빙 대상이 아님 (없는 API는 404가 되도록)
                        if (resourcePath.startsWith("api/")) {
                            return null;
                        }
                        Resource requested = location.createRelative(resourcePath);
                        if (requested.exists() && requested.isReadable()) {
                            return requested;
                        }
                        // 실제 파일이 없는 경로는 SPA 진입점으로 fallback
                        return new ClassPathResource("static/index.html");
                    }
                });
    }
}

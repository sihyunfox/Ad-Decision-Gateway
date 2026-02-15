package com.adg.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 경로 리다이렉트: swagger-ui.html → /docs, /actuator/ → /actuator (끝 슬래시 정규화).
 */
@Configuration
public class SwaggerRedirectConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/swagger-ui.html", "/docs");
        registry.addRedirectViewController("/actuator/", "/actuator");
    }
}

package com.adg.event.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Event 유효성 검증 단계 기본 구현. (구조만: 로깅만 수행)
 */
@Component
public class DefaultEventValidationStage implements EventValidationStage {

    private static final Logger log = LoggerFactory.getLogger(DefaultEventValidationStage.class);

    @Override
    public void validate(String url, String eventType) {
        log.info("Event validation: eventType={}, url={}", eventType, url);
    }
}

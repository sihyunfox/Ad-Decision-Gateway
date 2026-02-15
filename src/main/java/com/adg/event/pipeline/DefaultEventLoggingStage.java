package com.adg.event.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Event 로깅 단계 기본 구현. (로깅만 수행, 저장은 EventService에서 호출 후 처리)
 */
@Component
public class DefaultEventLoggingStage implements EventLoggingStage {

    private static final Logger log = LoggerFactory.getLogger(DefaultEventLoggingStage.class);

    @Override
    public void log(String url, String eventType) {
        log.info("Event logging: eventType={}, url={}", eventType, url);
    }
}

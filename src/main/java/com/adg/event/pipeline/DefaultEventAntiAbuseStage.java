package com.adg.event.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Event 어뷰징 적용 단계 기본 구현. (구조만: 로깅만 수행)
 */
@Component
public class DefaultEventAntiAbuseStage implements EventAntiAbuseStage {

    private static final Logger log = LoggerFactory.getLogger(DefaultEventAntiAbuseStage.class);

    @Override
    public void apply(String url, String eventType) {
        log.info("Event anti-abuse: eventType={}, url={}", eventType, url);
    }
}

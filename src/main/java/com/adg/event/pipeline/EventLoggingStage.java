package com.adg.event.pipeline;

/**
 * Event 로깅 단계.
 * <p>수신 URL·eventType 로깅. 실제 DB 저장은 EventService에서 event_url_log로 수행.</p>
 */
@FunctionalInterface
public interface EventLoggingStage {

    /** url, eventType 로깅(로그 출력). */
    void log(String url, String eventType);
}

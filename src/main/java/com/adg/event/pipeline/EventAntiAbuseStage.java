package com.adg.event.pipeline;

/**
 * Event 어뷰징 적용 단계.
 * <p>확장 시 rate limit, 블랙리스트 등 적용. 현재 기본 구현은 로깅만 수행.</p>
 */
@FunctionalInterface
public interface EventAntiAbuseStage {

    /** url·eventType에 대한 어뷰징 정책 적용. */
    void apply(String url, String eventType);
}

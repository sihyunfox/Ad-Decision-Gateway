package com.adg.event.pipeline;

/**
 * Event URL 유효성 검증 단계.
 * <p>확장 시 URL 형식·길이·허용 도메인 등 검증. 현재 기본 구현은 로깅만 수행.</p>
 */
@FunctionalInterface
public interface EventValidationStage {

    /** url과 eventType에 대한 유효성 검사. 실패 시 예외 가능. */
    void validate(String url, String eventType);
}

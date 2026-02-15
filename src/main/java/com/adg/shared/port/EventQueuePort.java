package com.adg.shared.port;

/**
 * 이벤트 큐(인메모리)에 이벤트를 적재하기 위한 애플리케이션 포트.
 * <p>
 * append 호출 시 스레드 세이프한 큐에만 적재. Drainer가 주기적으로 꺼내 파일(또는 Kafka)로 기록.
 */
public interface EventQueuePort {

    /**
     * 이벤트 한 건을 이벤트 큐에 적재한다.
     *
     * @param eventType  이벤트 유형 (예: decision-created, event-url-log, audit)
     * @param payloadJson 페이로드 JSON 문자열
     */
    void append(String eventType, String payloadJson);
}

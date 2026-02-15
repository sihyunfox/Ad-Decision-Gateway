package com.adg.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ADG 전용 Micrometer 메트릭 등록 및 보관.
 * <p>
 * 등록 메트릭:
 * <ul>
 *   <li>adg.decision.latency: Decision 요청 처리 소요 시간 (Timer)</li>
 *   <li>adg.decision.requests: Decision 요청 수 (Counter, result=success)</li>
 *   <li>adg.decision.fallback: fallback 사용 횟수 (Counter)</li>
 *   <li>adg.eventQueue.size: 이벤트 큐 크기 (Gauge, HealthIndicator/Drainer에서 갱신)</li>
 * </ul>
 * Prometheus 스크래핑 시 RED 및 이벤트 큐 크기 모니터링에 사용.
 */
@Component
public class AdgMetrics {

    private final Timer decisionLatency;
    private final Counter decisionRequests;
    private final Counter decisionFallback;
    private final AtomicInteger eventQueueSize;

    public AdgMetrics(MeterRegistry registry) {
        this.decisionLatency = Timer.builder("adg.decision.latency")
                .description("Decision 요청 처리 지연 시간")
                .register(registry);
        this.decisionRequests = Counter.builder("adg.decision.requests")
                .description("Decision 요청 총 건수")
                .tag("result", "success")
                .register(registry);
        this.decisionFallback = Counter.builder("adg.decision.fallback")
                .description("Decision fallback 사용 횟수")
                .register(registry);
        this.eventQueueSize = new AtomicInteger(0);
        registry.gauge("adg.eventQueue.size", eventQueueSize, AtomicInteger::get);
    }

    public Timer getDecisionLatency() {
        return decisionLatency;
    }

    public Counter getDecisionRequests() {
        return decisionRequests;
    }

    public Counter getDecisionFallback() {
        return decisionFallback;
    }

    /**
     * 이벤트 큐 크기를 갱신. EventQueueHealthIndicator/Drainer에서 주기적으로 호출.
     */
    public void setEventQueueSize(int count) {
        this.eventQueueSize.set(count);
    }
}

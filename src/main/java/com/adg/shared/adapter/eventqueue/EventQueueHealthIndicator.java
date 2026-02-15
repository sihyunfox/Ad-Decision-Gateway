package com.adg.shared.adapter.eventqueue;

import com.adg.config.AdgMetrics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * 인메모리 이벤트 큐 크기 기반 Health Indicator. DB 미사용.
 */
@Component
public class EventQueueHealthIndicator implements HealthIndicator {

    private final InMemoryEventQueue eventQueue;
    private final AdgMetrics metrics;

    @Value("${app.event-queue.backlog-warning-threshold:5000}")
    private int backlogWarningThreshold;

    public EventQueueHealthIndicator(InMemoryEventQueue eventQueue, AdgMetrics metrics) {
        this.eventQueue = eventQueue;
        this.metrics = metrics;
    }

    @Override
    public Health health() {
        int pending = eventQueue.size();
        metrics.setEventQueueSize(pending);

        if (pending > backlogWarningThreshold) {
            return Health.down()
                    .withDetail("eventQueue.size", pending)
                    .withDetail("threshold", backlogWarningThreshold)
                    .build();
        }
        if (pending > backlogWarningThreshold / 2) {
            return Health.status("DEGRADED")
                    .withDetail("eventQueue.size", pending)
                    .build();
        }
        return Health.up().withDetail("eventQueue.size", pending).build();
    }
}

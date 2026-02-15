package com.adg.shared.adapter.eventqueue;

import com.adg.config.AdgMetrics;
import com.adg.shared.dto.EventItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 인메모리 이벤트 큐를 주기적으로 drain 하여 파일(NDJSON)에 한 줄씩 기록. eventType + payload 한 줄.
 */
@Component
public class EventQueueDrainer {

    private static final Logger log = LoggerFactory.getLogger(EventQueueDrainer.class);

    private final InMemoryEventQueue eventQueue;
    private final AdgMetrics metrics;
    private final EventQueueFileWriter fileWriter;
    private final int drainBatchSize;
    private volatile boolean running = true;

    public EventQueueDrainer(InMemoryEventQueue eventQueue,
                             AdgMetrics metrics,
                             EventQueueFileWriter fileWriter,
                             @Value("${app.event-queue.drain-batch-size:500}") int drainBatchSize) {
        this.eventQueue = eventQueue;
        this.metrics = metrics;
        this.fileWriter = fileWriter;
        this.drainBatchSize = drainBatchSize;
    }

    @Scheduled(fixedDelayString = "${app.event-queue.drain-interval-ms:500}")
    public void drain() {
        if (!running) {
            return;
        }
        List<EventItem> batch = new ArrayList<>(drainBatchSize);
        eventQueue.drainTo(batch, drainBatchSize);
        if (batch.isEmpty()) {
            metrics.setEventQueueSize(eventQueue.size());
            return;
        }
        try {
            fileWriter.appendBatch(batch);
        } catch (IOException e) {
            log.warn("Event queue drain write failed: {}", e.getMessage());
        }
        metrics.setEventQueueSize(eventQueue.size());
    }

    /**
     * Shutdown 시 drain 중단 플래그 설정.
     */
    public void stopScheduling() {
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }
}

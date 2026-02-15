package com.adg.shared.adapter.eventqueue;

import com.adg.shared.dto.EventItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Graceful shutdown 시 이벤트 큐 스케줄 중단 후 큐에 남은 항목을 전부 drain 하여 파일에 기록.
 */
@Component
public class EventQueueShutdownHandler implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(EventQueueShutdownHandler.class);

    private final InMemoryEventQueue eventQueue;
    private final EventQueueDrainer drainer;
    private final EventQueueFileWriter fileWriter;
    private final int drainBatchSize;
    private final int shutdownDrainTimeoutSec;
    private volatile boolean running = false;

    public EventQueueShutdownHandler(InMemoryEventQueue eventQueue,
                                     EventQueueDrainer drainer,
                                     EventQueueFileWriter fileWriter,
                                     @Value("${app.event-queue.drain-batch-size:500}") int drainBatchSize,
                                     @Value("${app.event-queue.shutdown-drain-timeout-sec:30}") int shutdownDrainTimeoutSec) {
        this.eventQueue = eventQueue;
        this.drainer = drainer;
        this.fileWriter = fileWriter;
        this.drainBatchSize = drainBatchSize;
        this.shutdownDrainTimeoutSec = shutdownDrainTimeoutSec;
    }

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        if (!running) {
            return;
        }
        running = false;
        drainer.stopScheduling();
        long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(shutdownDrainTimeoutSec);
        int totalDrained = 0;
        try {
            List<EventItem> batch = new ArrayList<>(drainBatchSize);
            while (System.currentTimeMillis() < deadline) {
                int n = eventQueue.drainTo(batch, drainBatchSize);
                if (n == 0) {
                    if (eventQueue.isEmpty()) {
                        break;
                    }
                    Thread.sleep(50);
                    continue;
                }
                fileWriter.appendBatch(batch);
                totalDrained += n;
                batch.clear();
            }
            if (!eventQueue.isEmpty()) {
                log.warn("Event queue shutdown drain timeout: {} items remaining", eventQueue.size());
            } else if (totalDrained > 0) {
                log.info("Event queue shutdown drain completed: {} items written", totalDrained);
            }
        } catch (IOException e) {
            log.warn("Shutdown drain write failed: {}", e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Shutdown drain interrupted");
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}

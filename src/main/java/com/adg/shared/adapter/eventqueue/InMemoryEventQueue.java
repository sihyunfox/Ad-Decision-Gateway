package com.adg.shared.adapter.eventqueue;

import com.adg.shared.dto.EventItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 스레드 세이프한 인메모리 이벤트 큐. 용량 초과 시 가장 오래된 항목을 제거하고 offer(drop oldest).
 */
@Component
public class InMemoryEventQueue {

    private final LinkedBlockingQueue<EventItem> queue;
    private final int capacity;
    private final AtomicLong droppedCount = new AtomicLong(0);

    public InMemoryEventQueue(
            @Value("${app.event-queue.capacity:50000}") int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    /**
     * 큐에 이벤트 추가. 용량 초과 시 가장 오래된 항목을 poll 후 offer(drop oldest).
     */
    public void offer(EventItem item) {
        if (item == null) {
            return;
        }
        if (!queue.offer(item)) {
            queue.poll();
            droppedCount.incrementAndGet();
            queue.offer(item);
        }
    }

    /**
     * 최대 maxElements만큼 큐에서 꺼내서 주어진 리스트에 채움. drainTo와 동일한 스레드 세이프.
     */
    public int drainTo(java.util.Collection<? super EventItem> target, int maxElements) {
        if (maxElements <= 0) {
            return 0;
        }
        int count = 0;
        EventItem e;
        while (count < maxElements && (e = queue.poll()) != null) {
            target.add(e);
            count++;
        }
        return count;
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public long getDroppedCount() {
        return droppedCount.get();
    }
}

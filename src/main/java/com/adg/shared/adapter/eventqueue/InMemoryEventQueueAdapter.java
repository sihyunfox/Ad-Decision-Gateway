package com.adg.shared.adapter.eventqueue;

import com.adg.shared.dto.EventItem;
import com.adg.shared.port.EventQueuePort;
import org.springframework.stereotype.Component;

/**
 * EventQueuePort 구현체. 이벤트를 인메모리 큐에만 적재. DB 미사용.
 */
@Component
public class InMemoryEventQueueAdapter implements EventQueuePort {

    private final InMemoryEventQueue eventQueue;

    public InMemoryEventQueueAdapter(InMemoryEventQueue eventQueue) {
        this.eventQueue = eventQueue;
    }

    @Override
    public void append(String eventType, String payloadJson) {
        if (eventType == null || payloadJson == null) {
            return;
        }
        eventQueue.offer(new EventItem(eventType, payloadJson));
    }
}

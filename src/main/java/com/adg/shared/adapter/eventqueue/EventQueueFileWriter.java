package com.adg.shared.adapter.eventqueue;

import com.adg.shared.dto.EventItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * 이벤트 큐 배치를 파일(NDJSON 형식: eventType + TAB + payloadJson 한 줄씩)에 append.
 * EventQueueDrainer와 EventQueueShutdownHandler에서 공통 사용.
 */
@Component
public class EventQueueFileWriter {

    private final Path filePath;

    public EventQueueFileWriter(@Value("${app.event-queue.event-file:logs/events.ndjson}") String eventFile) {
        this.filePath = Paths.get(eventFile).toAbsolutePath();
    }

    /**
     * 배치를 파일에 한 줄씩 추가. 디렉터리 없으면 생성.
     */
    public void appendBatch(List<EventItem> batch) throws IOException {
        if (batch == null || batch.isEmpty()) {
            return;
        }
        Path parent = filePath.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        StringBuilder sb = new StringBuilder();
        for (EventItem item : batch) {
            sb.append(item.getEventType()).append('\t').append(item.getPayloadJson()).append('\n');
        }
        Files.write(filePath, sb.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}

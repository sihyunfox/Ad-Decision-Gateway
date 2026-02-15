package com.adg.event.app;

import com.adg.shared.dto.EventUrlRequest;
import com.adg.shared.dto.EventUrlResponse;
import com.adg.shared.port.EventQueuePort;
import com.adg.event.pipeline.EventAntiAbuseStage;
import com.adg.event.pipeline.EventLoggingStage;
import com.adg.event.pipeline.EventValidationStage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Event API 서비스.
 * <p>URL 수신 시 3단계 파이프라인 실행: 유효성 검사 → 어뷰징 적용 → 로깅.
 * 이력은 EventQueuePort(인메모리 큐)에 event-url-log로 적재, Drainer가 파일로 기록.</p>
 */
@Service
public class EventService {

    private final EventValidationStage validationStage;
    private final EventAntiAbuseStage antiAbuseStage;
    private final EventLoggingStage loggingStage;
    private final EventQueuePort eventQueuePort;
    private final ObjectMapper objectMapper;

    public EventService(EventValidationStage validationStage,
                        EventAntiAbuseStage antiAbuseStage,
                        EventLoggingStage loggingStage,
                        EventQueuePort eventQueuePort,
                        ObjectMapper objectMapper) {
        this.validationStage = validationStage;
        this.antiAbuseStage = antiAbuseStage;
        this.loggingStage = loggingStage;
        this.eventQueuePort = eventQueuePort;
        this.objectMapper = objectMapper;
    }

    /**
     * 이벤트 URL 처리: 유효성 → 어뷰징 → 로깅 후 큐 적재, 동일 url 반환.
     * @param request url 필수
     * @param eventType impression | click | nurl | burl | lurl
     */
    public EventUrlResponse handleEvent(EventUrlRequest request, String eventType) {
        String url = request.getUrl();
        validationStage.validate(url, eventType);
        antiAbuseStage.apply(url, eventType);
        loggingStage.log(url, eventType);
        try {
            String payload = objectMapper.writeValueAsString(Map.of("eventType", eventType, "url", url));
            eventQueuePort.append("event-url-log", payload);
        } catch (JsonProcessingException e) {
            // non-blocking: 로그만 하고 응답은 정상 반환
        }
        return EventUrlResponse.builder().url(url).build();
    }
}

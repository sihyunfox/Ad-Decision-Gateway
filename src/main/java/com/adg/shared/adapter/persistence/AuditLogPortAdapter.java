package com.adg.shared.adapter.persistence;

import com.adg.shared.port.EventQueuePort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 감사(audit) 로그 어댑터. Policy/Admin 정책 수정 시 이력을 인메모리 큐에 audit 이벤트로 적재. DB 미사용.
 */
@Component
public class AuditLogPortAdapter {

    private final EventQueuePort eventQueuePort;
    private final ObjectMapper objectMapper;

    public AuditLogPortAdapter(EventQueuePort eventQueuePort, ObjectMapper objectMapper) {
        this.eventQueuePort = eventQueuePort;
        this.objectMapper = objectMapper;
    }

    public void log(String entityType, String entityId, String action, String oldValue, String newValue, String actor) {
        try {
            String payload = objectMapper.writeValueAsString(Map.of(
                    "entityType", entityType != null ? entityType : "",
                    "entityId", entityId != null ? entityId : "",
                    "action", action != null ? action : "",
                    "oldValue", oldValue != null ? oldValue : "",
                    "newValue", newValue != null ? newValue : "",
                    "actor", actor != null ? actor : ""
            ));
            eventQueuePort.append("audit", payload);
        } catch (JsonProcessingException e) {
            // non-blocking: 로그 실패해도 정책 업데이트는 유지
        }
    }
}

package com.adg.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 인메모리 이벤트 큐에 적재되는 한 건. eventType + payloadJson.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventItem {

    private String eventType;
    private String payloadJson;
}

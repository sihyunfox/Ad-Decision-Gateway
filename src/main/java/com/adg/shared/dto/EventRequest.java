package com.adg.shared.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Event API(노출/클릭) 요청 바디 DTO.
 * <p>
 * eventId는 idempotency key로 필수. 동일 eventId 재전송 시 기존 레코드 유지.
 * meta 내 device/user 식별자는 저장·로그 시 마스킹 적용.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {

    @NotBlank(message = "eventId is required for idempotency")
    private String eventId;

    private String decisionId;
    private String clientId;
    private Instant timestamp;
    /** 이벤트 메타데이터 (deviceId, userId 등 포함 가능, 민감 필드 마스킹 대상). */
    private Map<String, Object> meta;

    /** OpenRTB 소재 ID (creative id). */
    private String crid;
    /** OpenRTB 노출 단위 ID (imp id). */
    private String impid;
    /** 매체 사이트 ID. */
    private String siteId;
    /** 매체 앱 ID. */
    private String appId;
}

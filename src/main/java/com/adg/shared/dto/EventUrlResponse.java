package com.adg.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event API 응답 — 요청과 동일한 URL 반환.
 * <p>요구사항: 요청/응답 동일. 필드 url만 포함.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventUrlResponse {

    private String url;
}

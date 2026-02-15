package com.adg.shared.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event API(노출/클릭/nurl/burl/lurl) 요청 — URL 수신.
 * <p>5종 엔드포인트 공통: body에 url 필수(@NotBlank).</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventUrlRequest {

    @NotBlank(message = "url is required")
    private String url;
}

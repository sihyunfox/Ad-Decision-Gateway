package com.adg.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Decision API 응답 바디 DTO.
 * <p>
 * 우승 캠페인/크리에이티브가 없으면 winner는 null. fallback 사용 시 fallbackUsed=true.
 * request 시 debug=true이면 debug 필드에 추가 정보 포함.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DecisionResponse {

    private String decisionId;
    private String traceId;
    private String requestId;
    private WinnerInfo winner;
    private boolean fallbackUsed;
    private Object debug;

    /** 선정된 광고(캠페인·크리에이티브) 정보. */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WinnerInfo {
        private String campaignId;
        private String creativeId;
        private BigDecimal bid;
        private BigDecimal score;
        private String trackingUrl;
    }
}

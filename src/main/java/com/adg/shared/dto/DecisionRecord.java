package com.adg.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Decision 처리 결과를 decision_history에 저장할 때 사용하는 레코드 DTO.
 * <p>
 * DecisionHistoryPort.save(DecisionRecord)로 전달되며, Adapter에서 Entity로 변환 후 persist.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionRecord {

    private String decisionId;
    private String traceId;
    private String requestId;
    private String clientId;
    private String placementId;
    private String winnerCampaignId;
    private String winnerCreativeId;
    private BigDecimal winnerBid;
    private BigDecimal winnerScore;
    private String trackingUrl;
    private boolean fallbackUsed;
}

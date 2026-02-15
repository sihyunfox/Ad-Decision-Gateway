package com.adg.shared.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * decision_history 테이블에 대응하는 JPA 엔티티.
 */
@Entity
@Table(name = "decision_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecisionHistoryEntity extends BaseEntity {

    @Id
    @Column(name = "decision_id", length = 64)
    private String decisionId;

    @Column(name = "trace_id", nullable = false, length = 64)
    private String traceId;

    @Column(name = "request_id", length = 64)
    private String requestId;

    @Column(name = "client_id", nullable = false, length = 64)
    private String clientId;

    @Column(name = "placement_id", nullable = false, length = 64)
    private String placementId;

    @Column(name = "winner_campaign_id", length = 64)
    private String winnerCampaignId;

    @Column(name = "winner_creative_id", length = 64)
    private String winnerCreativeId;

    @Column(name = "winner_bid", precision = 19, scale = 4)
    private BigDecimal winnerBid;

    @Column(name = "winner_score", precision = 19, scale = 4)
    private BigDecimal winnerScore;

    @Column(name = "tracking_url", length = 1024)
    private String trackingUrl;

    @Column(name = "fallback_used", nullable = false)
    private boolean fallbackUsed;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }
}

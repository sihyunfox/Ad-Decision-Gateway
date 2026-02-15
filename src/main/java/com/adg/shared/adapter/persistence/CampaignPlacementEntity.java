package com.adg.shared.adapter.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * campaign_placements 테이블에 대응하는 JPA 엔티티. placement별 후보 캠페인 1건.
 */
@Entity
@Table(name = "campaign_placements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignPlacementEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "placement_id", nullable = false, length = 64)
    private String placementId;

    @Column(name = "campaign_id", nullable = false, length = 64)
    private String campaignId;

    @Column(name = "creative_id", nullable = false, length = 64)
    private String creativeId;

    @Column(name = "status", nullable = false, length = 16)
    private String status;

    @Column(name = "budget", precision = 19, scale = 4)
    private BigDecimal budget;

    @Column(name = "bid", nullable = false, precision = 19, scale = 4)
    private BigDecimal bid;

    @Lob
    @Column(name = "target_conditions", columnDefinition = "CLOB")
    private String targetConditions;
}

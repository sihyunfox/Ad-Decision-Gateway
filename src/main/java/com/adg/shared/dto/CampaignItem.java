package com.adg.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Downstream Campaign 서비스에서 반환하는 캠페인(또는 크리에이티브) 한 건 DTO.
 * <p>
 * Decision 파이프라인에서 필터(CampaignFilter)·스코어링·우승자 선정(WinnerSelector)에 사용.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignItem {

    private String campaignId;
    private String creativeId;
    /** 캠페인 상태 (ACTIVE 등). 필터 시 ACTIVE만 통과. */
    private String status;
    private BigDecimal budget;
    private String targetConditions;
    /** 입찰가. 우승자 선정 시 최대 bid 기준. */
    private BigDecimal bid;
}

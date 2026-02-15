package com.adg.decision.domain;

import com.adg.shared.dto.CampaignItem;
import com.adg.shared.dto.CapCheckResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 후보 캠페인 목록을 정책·Cap 기준으로 필터링하는 도메인 유틸리티.
 */
public final class CampaignFilter {

    private CampaignFilter() {
    }

    public static List<CampaignItem> filter(List<CampaignItem> campaigns, CapCheckResponse capResponse) {
        return campaigns.stream()
                .filter(c -> "ACTIVE".equalsIgnoreCase(c.getStatus()))
                .filter(c -> capResponse == null || capResponse.isAllow())
                .collect(Collectors.toList());
    }
}

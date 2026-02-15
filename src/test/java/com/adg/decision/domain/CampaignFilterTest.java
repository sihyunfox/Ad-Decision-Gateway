package com.adg.decision.domain;

import com.adg.shared.dto.CampaignItem;
import com.adg.shared.dto.CapCheckResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignFilterTest {

    @Test
    void filter_keepsActive_andAllowsWhenCapAllow() {
        List<CampaignItem> campaigns = List.of(
                campaign("c1", "ACTIVE"),
                campaign("c2", "PAUSED")
        );
        CapCheckResponse cap = CapCheckResponse.builder().allow(true).remaining(10).build();
        List<CampaignItem> result = CampaignFilter.filter(campaigns, cap);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCampaignId()).isEqualTo("c1");
    }

    @Test
    void filter_excludesAllWhenCapDeny() {
        List<CampaignItem> campaigns = List.of(campaign("c1", "ACTIVE"));
        CapCheckResponse cap = CapCheckResponse.builder().allow(false).remaining(0).build();
        List<CampaignItem> result = CampaignFilter.filter(campaigns, cap);
        assertThat(result).isEmpty();
    }

    @Test
    void filter_nullCapTreatsAsAllow() {
        List<CampaignItem> campaigns = List.of(campaign("c1", "ACTIVE"));
        List<CampaignItem> result = CampaignFilter.filter(campaigns, null);
        assertThat(result).hasSize(1);
    }

    private static CampaignItem campaign(String id, String status) {
        return CampaignItem.builder()
                .campaignId(id)
                .creativeId("cr-" + id)
                .status(status)
                .bid(BigDecimal.ONE)
                .build();
    }
}

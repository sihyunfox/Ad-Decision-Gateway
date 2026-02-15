package com.adg.decision.domain;

import com.adg.shared.dto.CampaignItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class WinnerSelectorTest {

    @Test
    void select_returnsHighestBid() {
        List<CampaignItem> candidates = List.of(
                CampaignItem.builder().campaignId("c1").bid(new BigDecimal("1.0")).build(),
                CampaignItem.builder().campaignId("c2").bid(new BigDecimal("2.5")).build(),
                CampaignItem.builder().campaignId("c3").bid(new BigDecimal("1.5")).build()
        );
        Optional<CampaignItem> winner = WinnerSelector.select(candidates);
        assertThat(winner).isPresent();
        assertThat(winner.get().getCampaignId()).isEqualTo("c2");
    }

    @Test
    void select_emptyWhenNoCandidates() {
        assertThat(WinnerSelector.select(List.of())).isEmpty();
        assertThat(WinnerSelector.select(null)).isEmpty();
    }
}

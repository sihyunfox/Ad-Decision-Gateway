package com.adg.decision.pipeline;

import com.adg.decision.domain.CampaignFilter;
import com.adg.shared.dto.CampaignItem;
import com.adg.shared.dto.CapCheckResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 광고 필터링 단계 기본 구현.
 * 기존 CampaignFilter를 호출하여 context의 candidates → filteredCandidates 설정.
 */
@Component
public class DefaultAdFilteringStage implements AdFilteringStage {

    @Override
    public void filter(AdDecisionPipelineContext context) {
        List<CampaignItem> candidates = context.getCandidates();
        CapCheckResponse capResponse = context.getCapResponse();
        List<CampaignItem> filtered = CampaignFilter.filter(
                candidates != null ? candidates : List.of(),
                capResponse
        );
        context.setFilteredCandidates(filtered);
    }
}

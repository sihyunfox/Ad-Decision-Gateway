package com.adg.decision.pipeline;

import com.adg.decision.domain.WinnerSelector;
import com.adg.shared.dto.CampaignItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 광고 선택 단계 기본 구현.
 * 기존 WinnerSelector를 호출하여 context에 winner 설정.
 */
@Component
public class DefaultAdSelectionStage implements AdSelectionStage {

    @Override
    public void select(AdDecisionPipelineContext context) {
        List<CampaignItem> filtered = context.getFilteredCandidates();
        Optional<CampaignItem> winner = WinnerSelector.selectWithAiScores(
                filtered != null ? filtered : List.of(),
                context.getAiScores());
        context.setWinner(winner);
        context.setFallbackUsed(winner.isEmpty());
    }
}

package com.adg.shared.adapter.persistence;

import com.adg.shared.dto.DecisionRecord;
import com.adg.shared.port.DecisionHistoryPort;
import org.springframework.stereotype.Component;

/**
 * DecisionHistoryPort 구현체. DecisionRecord를 decision_history 테이블에 저장한다.
 */
@Component
public class DecisionHistoryPortAdapter implements DecisionHistoryPort {

    private final DecisionHistoryRepository repository;

    public DecisionHistoryPortAdapter(DecisionHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(DecisionRecord record) {
        DecisionHistoryEntity entity = DecisionHistoryEntity.builder()
                .decisionId(record.getDecisionId())
                .traceId(record.getTraceId())
                .requestId(record.getRequestId())
                .clientId(record.getClientId())
                .placementId(record.getPlacementId())
                .winnerCampaignId(record.getWinnerCampaignId())
                .winnerCreativeId(record.getWinnerCreativeId())
                .winnerBid(record.getWinnerBid())
                .winnerScore(record.getWinnerScore())
                .trackingUrl(record.getTrackingUrl())
                .fallbackUsed(record.isFallbackUsed())
                .build();
        repository.save(entity);
    }
}

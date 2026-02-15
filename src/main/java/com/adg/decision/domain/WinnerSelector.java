package com.adg.decision.domain;

import com.adg.shared.dto.CampaignItem;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 필터 통과한 후보 캠페인 중 우승자를 선정하는 도메인 유틸리티.
 * AI 스코어가 있으면 (aiScore * bid) 기준, 없으면 bid 기준으로 선정.
 */
public final class WinnerSelector {

    private static final double DEFAULT_AI_SCORE = 0.5;

    private WinnerSelector() {
    }

    public static Optional<CampaignItem> select(List<CampaignItem> candidates) {
        return selectWithAiScores(candidates, null);
    }

    /**
     * AI 스코어 맵이 있으면 (aiScore * bid)로 정렬해 최대 후보 반환. 키는 "campaignId:creativeId".
     */
    public static Optional<CampaignItem> selectWithAiScores(List<CampaignItem> candidates, Map<String, Double> aiScores) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }
        return candidates.stream()
                .max(Comparator.comparing(c -> effectiveScore(c, aiScores)));
    }

    private static BigDecimal effectiveScore(CampaignItem c, Map<String, Double> aiScores) {
        BigDecimal bid = c.getBid() != null ? c.getBid() : BigDecimal.ZERO;
        if (aiScores == null || aiScores.isEmpty()) {
            return bid;
        }
        String key = c.getCampaignId() + ":" + c.getCreativeId();
        double ai = aiScores.getOrDefault(key, DEFAULT_AI_SCORE);
        return bid.multiply(BigDecimal.valueOf(ai));
    }
}

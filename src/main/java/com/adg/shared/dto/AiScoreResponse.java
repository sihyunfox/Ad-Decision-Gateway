package com.adg.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 엔진 스코어링 응답. 후보별 점수(0~1).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiScoreResponse {

    private List<ScoreEntry> scores;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreEntry {
        private String campaignId;
        private String creativeId;
        private double score;
    }
}

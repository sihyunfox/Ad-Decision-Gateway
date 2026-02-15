package com.adg.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 엔진 스코어링 요청. 후보 목록(campaignId, creativeId).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiScoreRequest {

    private List<CandidateKey> candidates;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CandidateKey {
        private String campaignId;
        private String creativeId;
    }
}

package com.adg.shared.adapter.web.mock;

import com.adg.shared.dto.AiScoreRequest;
import com.adg.shared.dto.AiScoreResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 광고 선택용 Mock AI 엔진 2개. 파이프라인 AiScoringStage에서 HTTP로 호출하는 샘플.
 */
@RestController
@RequestMapping("/mock/ai-engine")
public class MockAiEngineController {

    /**
     * Mock AI 엔진 1. 후보별 0.5~1.0 랜덤 점수 반환.
     */
    @PostMapping("/1")
    public ResponseEntity<AiScoreResponse> score1(@RequestBody AiScoreRequest request) {
        return ResponseEntity.ok(buildMockScores(request, 0.5, 1.0));
    }

    /**
     * Mock AI 엔진 2. 후보별 0.3~0.9 랜덤 점수 반환.
     */
    @PostMapping("/2")
    public ResponseEntity<AiScoreResponse> score2(@RequestBody AiScoreRequest request) {
        return ResponseEntity.ok(buildMockScores(request, 0.3, 0.9));
    }

    private static AiScoreResponse buildMockScores(AiScoreRequest request, double minScore, double maxScore) {
        List<AiScoreResponse.ScoreEntry> entries = new ArrayList<>();
        if (request.getCandidates() != null) {
            for (AiScoreRequest.CandidateKey c : request.getCandidates()) {
                double score = minScore + (maxScore - minScore) * ThreadLocalRandom.current().nextDouble();
                entries.add(AiScoreResponse.ScoreEntry.builder()
                        .campaignId(c.getCampaignId())
                        .creativeId(c.getCreativeId())
                        .score(score)
                        .build());
            }
        }
        return AiScoreResponse.builder().scores(entries).build();
    }
}

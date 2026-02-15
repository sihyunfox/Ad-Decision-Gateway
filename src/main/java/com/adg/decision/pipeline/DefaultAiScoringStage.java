package com.adg.decision.pipeline;

import com.adg.shared.dto.AiScoreRequest;
import com.adg.shared.dto.AiScoreResponse;
import com.adg.shared.dto.CampaignItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * AiScoringStage 기본 구현. Mock AI 엔진 2개(HTTP) 호출 후 점수 평균해 context.aiScores 설정.
 * 동시 HTTP 호출 수는 decisionExecutor로 제한.
 */
@Component
public class DefaultAiScoringStage implements AiScoringStage {

    private static final Logger log = LoggerFactory.getLogger(DefaultAiScoringStage.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final Executor executor;

    public DefaultAiScoringStage(RestTemplate restTemplate,
                                @Value("${app.mock.base-url:http://localhost:8080}") String baseUrl,
                                @Qualifier("decisionExecutor") Executor executor) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.executor = executor;
    }

    @Override
    public void score(AdDecisionPipelineContext context) {
        List<CampaignItem> filtered = context.getFilteredCandidates();
        if (filtered == null || filtered.isEmpty()) {
            context.setAiScores(Collections.emptyMap());
            return;
        }

        AiScoreRequest request = AiScoreRequest.builder()
                .candidates(filtered.stream()
                        .map(c -> AiScoreRequest.CandidateKey.builder()
                                .campaignId(c.getCampaignId())
                                .creativeId(c.getCreativeId())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        String url1 = baseUrl + "mock/ai-engine/1";
        String url2 = baseUrl + "mock/ai-engine/2";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AiScoreRequest> entity = new HttpEntity<>(request, headers);

        CompletableFuture<AiScoreResponse> future1 = CompletableFuture.supplyAsync(() -> callEngine(url1, entity), executor);
        CompletableFuture<AiScoreResponse> future2 = CompletableFuture.supplyAsync(() -> callEngine(url2, entity), executor);

        AiScoreResponse res1 = future1.join();
        AiScoreResponse res2 = future2.join();

        Map<String, Double> merged = mergeScores(res1, res2);
        context.setAiScores(merged);
    }

    private AiScoreResponse callEngine(String url, HttpEntity<AiScoreRequest> entity) {
        try {
            return restTemplate.postForObject(url, entity.getBody(), AiScoreResponse.class);
        } catch (Exception e) {
            log.warn("AI engine call failed: url={}, {}", url, e.getMessage());
            return AiScoreResponse.builder().scores(Collections.emptyList()).build();
        }
    }

    private static Map<String, Double> mergeScores(AiScoreResponse res1, AiScoreResponse res2) {
        Map<String, Double> sum = new HashMap<>();
        Map<String, Integer> count = new HashMap<>();
        addScores(res1, sum, count);
        addScores(res2, sum, count);
        Map<String, Double> avg = new HashMap<>();
        sum.forEach((key, s) -> {
            int c = count.getOrDefault(key, 1);
            avg.put(key, c > 0 ? s / c : 0.5);
        });
        return avg;
    }

    private static void addScores(AiScoreResponse res, Map<String, Double> sum, Map<String, Integer> count) {
        if (res == null || res.getScores() == null) return;
        for (AiScoreResponse.ScoreEntry e : res.getScores()) {
            String key = e.getCampaignId() + ":" + e.getCreativeId();
            sum.merge(key, e.getScore(), Double::sum);
            count.merge(key, 1, Integer::sum);
        }
    }
}

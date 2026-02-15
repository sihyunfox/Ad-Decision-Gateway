package com.adg.decision.pipeline;

/**
 * 파이프라인 단계: 필터 통과 후보에 대해 AI 엔진(HTTP) 호출 후 context.aiScores 설정.
 */
@FunctionalInterface
public interface AiScoringStage {

    void score(AdDecisionPipelineContext context);
}

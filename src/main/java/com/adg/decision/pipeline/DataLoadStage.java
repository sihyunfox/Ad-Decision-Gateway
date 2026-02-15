package com.adg.decision.pipeline;

/**
 * 파이프라인 1단계: Profile/Campaign/Policy/Cap 데이터 로드(DB·캐시).
 * context.request에서 placementId, userId, clientId를 추출해 포트 호출 후 context에 설정.
 */
@FunctionalInterface
public interface DataLoadStage {

    void load(AdDecisionPipelineContext context);
}

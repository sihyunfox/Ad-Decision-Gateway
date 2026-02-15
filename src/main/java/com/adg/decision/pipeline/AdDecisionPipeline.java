package com.adg.decision.pipeline;

import org.springframework.stereotype.Component;

/**
 * 광고 의사결정 파이프라인 오케스트레이터.
 * 데이터 로드(DB·캐시) → 요청 유효성 검증 → 광고 필터링 → AI 스코어링 → 광고 선택 → 노출 준비 순으로 단계 실행.
 */
@Component
public class AdDecisionPipeline {

    private final DataLoadStage dataLoadStage;
    private final RequestValidationStage requestValidationStage;
    private final AdFilteringStage adFilteringStage;
    private final AiScoringStage aiScoringStage;
    private final AdSelectionStage adSelectionStage;
    private final ExposureStage exposureStage;

    public AdDecisionPipeline(DataLoadStage dataLoadStage,
                              RequestValidationStage requestValidationStage,
                              AdFilteringStage adFilteringStage,
                              AiScoringStage aiScoringStage,
                              AdSelectionStage adSelectionStage,
                              ExposureStage exposureStage) {
        this.dataLoadStage = dataLoadStage;
        this.requestValidationStage = requestValidationStage;
        this.adFilteringStage = adFilteringStage;
        this.aiScoringStage = aiScoringStage;
        this.adSelectionStage = adSelectionStage;
        this.exposureStage = exposureStage;
    }

    /**
     * 파이프라인을 순서대로 실행한다.
     *
     * @param context request, clientId 등 최소 정보만 담긴 컨텍스트. DataLoadStage가 candidates, capResponse 등을 채운다.
     */
    public void run(AdDecisionPipelineContext context) {
        dataLoadStage.load(context);
        requestValidationStage.validate(context);
        adFilteringStage.filter(context);
        aiScoringStage.score(context);
        adSelectionStage.select(context);
        exposureStage.prepareForExposure(context);
    }
}

package com.adg.decision.pipeline;

import org.springframework.stereotype.Component;

/**
 * 노출 준비 단계 기본 구현.
 * 스켈레톤: 로직 없음. 추후 노출을 위한 응답/기록 준비, 이벤트 큐 적재 등을 추가할 수 있음.
 */
@Component
public class DefaultExposureStage implements ExposureStage {

    @Override
    public void prepareForExposure(AdDecisionPipelineContext context) {
        // 스켈레톤: 노출을 위한 응답/기록 준비 확장 지점. 실제 저장·이벤트 큐 적재는 DecisionService에서 수행.
    }
}

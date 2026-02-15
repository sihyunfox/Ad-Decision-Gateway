package com.adg.decision.pipeline;

/**
 * 노출 준비 단계.
 * <p>결과 저장·응답 구성·노출 이벤트 준비 등. 스켈레톤에서는 시그니처만 두고 확장 시 로직 추가.</p>
 */
@FunctionalInterface
public interface ExposureStage {

    /** 노출을 위한 최종 준비(응답/기록). 실제 저장·이벤트 큐 적재는 DecisionService에서 수행 */
    void prepareForExposure(AdDecisionPipelineContext context);
}

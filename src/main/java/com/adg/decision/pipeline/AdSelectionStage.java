package com.adg.decision.pipeline;

/**
 * 광고 선택(우승자 선정) 단계.
 * <p>context의 filteredCandidates를 사용해 winner를 설정한다. 없으면 fallbackUsed=true.</p>
 */
@FunctionalInterface
public interface AdSelectionStage {

    /** 스코어/입찰가 기준 우승자 선정 후 context.setWinner, setFallbackUsed 설정 */
    void select(AdDecisionPipelineContext context);
}

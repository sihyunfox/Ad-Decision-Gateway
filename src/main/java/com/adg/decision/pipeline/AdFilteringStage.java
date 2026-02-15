package com.adg.decision.pipeline;

/**
 * 후보 광고 필터링 단계.
 * <p>context의 candidates와 capResponse를 사용해 filteredCandidates를 설정한다.</p>
 */
@FunctionalInterface
public interface AdFilteringStage {

    /** 정책/Cap 기반으로 후보 필터링 후 context.setFilteredCandidates 호출 */
    void filter(AdDecisionPipelineContext context);
}

package com.adg.decision.pipeline;

/**
 * 광고 요청 유효성 검증 단계.
 * <p>실패 시 예외를 던진다. 확장 시 Bean Validation 또는 도메인 규칙 추가 가능.</p>
 */
@FunctionalInterface
public interface RequestValidationStage {

    /** 컨텍스트 내 request 필수 필드(clientId, placementId) 검증. 실패 시 IllegalArgumentException */
    void validate(AdDecisionPipelineContext context);
}

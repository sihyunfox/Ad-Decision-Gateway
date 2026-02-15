package com.adg.decision.pipeline;

import com.adg.shared.dto.DecisionRequest;
import org.springframework.stereotype.Component;

/**
 * 요청 유효성 검증 단계 기본 구현.
 * 스켈레톤: 필수 필드(clientId, placementId) null/blank 체크. Controller에서 Bean Validation 적용 시 중복될 수 있어 최소한만 수행.
 */
@Component
public class DefaultRequestValidationStage implements RequestValidationStage {

    @Override
    public void validate(AdDecisionPipelineContext context) {
        DecisionRequest request = context.getRequest();
        if (request == null) {
            throw new IllegalArgumentException("request is required");
        }
        if (request.getClientId() == null || request.getClientId().isBlank()) {
            throw new IllegalArgumentException("clientId is required");
        }
        if (request.getPlacementId() == null || request.getPlacementId().isBlank()) {
            throw new IllegalArgumentException("placementId is required");
        }
    }
}

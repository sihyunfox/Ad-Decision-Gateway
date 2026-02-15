package com.adg.shared.adapter.persistence;

import com.adg.shared.dto.PolicyResponse;
import com.adg.shared.port.PolicyPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PolicyPort 구현체. policies 테이블 조회. 1분 캐시 적용.
 * app.decision.use-http-dependencies=false(기본)일 때 활성화.
 */
@Component
@ConditionalOnProperty(name = "app.decision.use-http-dependencies", havingValue = "false", matchIfMissing = true)
public class PolicyPersistenceAdapter implements PolicyPort {

    private final PolicyRepository repository;

    public PolicyPersistenceAdapter(PolicyRepository repository) {
        this.repository = repository;
    }

    @Override
    @Cacheable(cacheNames = "policies", cacheManager = "decisionCacheManager")
    public List<PolicyResponse> getPolicies(String clientId) {
        List<PolicyEntity> list = repository.findByClientIdAndActiveTrue(clientId);
        if (list == null || list.isEmpty()) return Collections.emptyList();
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private PolicyResponse toResponse(PolicyEntity e) {
        return PolicyResponse.builder()
                .clientId(e.getClientId())
                .filterRules(e.getFilterRules())
                .abFlags(e.getAbFlags())
                .timeoutConfig(e.getTimeoutConfig())
                .build();
    }
}
